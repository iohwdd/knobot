package com.iohw.knobot.chat.ai.config;

import com.alibaba.dashscope.tokenizers.Tokenizer;
import com.alibaba.dashscope.tokenizers.TokenizerFactory;
import com.alibaba.fastjson.JSONObject;
import com.iohw.knobot.chat.ai.service.ChatCompressionService;
import com.iohw.knobot.chat.domain.entity.ChatMessageDO;
import com.iohw.knobot.chat.mapper.ChatMessageMapper;
import com.iohw.knobot.utils.IdGeneratorUtil;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;

/**
 * @author: iohw
 * @date: 2025/4/13 10:35
 * @description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PersistentChatMemoryStore implements ChatMemoryStore {
    // Redis 键前缀配置
    private static final String COMPRESSED_KEY_PREFIX = "chat:compressed:";
    private static final String COMPRESSION_TIME_KEY_PREFIX = "chat:compressed:last_time:";

    // 对话上下文配置
    @Value("${chat.context.window-size}")
    private int CONTEXT_WINDOW_SIZE;
    @Value("${chat.context.expire-hours}")
    private int CONTEXT_EXPIRE;
    @Value("${chat.context.compression-token-threshold}")
    private int MAX_TOKEN_THRESHOLD;
    @Value("${chat.context.compress-min-interval-minutes}")
    private int COMPRESS_MIN_INTERVAL_MINUTES;
    private int COMPRESS_THRESHOLD;


    private final ChatMessageMapper chatMessageMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChatCompressionService chatCompressionService;
    private final Tokenizer tokenizer = TokenizerFactory.qwen();


    @Override
    public List<ChatMessage> getMessages(Object o) {
        String memoryId = (String) o;

        // 1. 尝试从 Redis 获取压缩上下文
        String key = COMPRESSED_KEY_PREFIX + memoryId;
        String compressedJson = stringRedisTemplate.opsForValue().get(key);

        List<ChatMessage> messages;
        if (StringUtils.hasText(compressedJson)) {
            // 从 Redis 获取压缩后的上下文
            messages = ChatMessageDeserializer.messagesFromJson(compressedJson);
            log.info("从 Redis 获取压缩上下文，memoryId: {}, 消息数：{}", memoryId, messages.size());
        } else {
            // 从 MySQL 重建完整对话并压缩
            messages = rebuildAndCompressFromDatabase(memoryId);
            log.info("从 MySQL 重建并压缩，memoryId: {}, 消息数：{}", memoryId, messages.size());
        }


        return messages;
    }

    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {
        String memoryId = o.toString();

        // 1. 最后一条消息即最新消息，保存到 MySQL（原始数据完整存储）
        ChatMessage latestMessage = list.getLast();
        String role = getRoleFromMessage(latestMessage);
        String content = getContentMessage(latestMessage);

        // 区分原始内容和增强内容
        String originalContent = content;
        String enhancedContent = null;
        int userTokens = 0;
        int systemTokens = 0;
        // todo 不优雅
        if (isUserMessageEnhanced(content)) {
            // 这是一个RAG增强的消息
            originalContent = extractOriginalContent(content);
            enhancedContent = content;  // 完整的增强内容
            userTokens = calculateToken(originalContent);      // 用户原始问题的token
            systemTokens = calculateToken(enhancedContent);    // 系统实际处理的token（包含RAG内容）
        } else {
            // 普通消息
            userTokens = calculateToken(originalContent);
            systemTokens = calculateToken(originalContent);    // 普通消息两者相同
        }

        // 保存到 MySQL（原始数据）
        ChatMessageDO chatMessageDO = ChatMessageDO.builder()
            .memoryId(memoryId)
            .messageId(IdGeneratorUtil.generateId())
            .role(role)
            .content(originalContent)      // 存储原始内容
            .enhancedContent(enhancedContent)  // 存储增强内容（如果有的话）
            .userTokens(userTokens)        // 用户层面token（原始内容）
            .systemTokens(systemTokens)    // 系统实际消耗token（增强内容或原始内容）
            .build();

        chatMessageMapper.insert(chatMessageDO);

        // 2. 判断是否需要压缩并更新 Redis（如果是AI回复，表示一轮对话完成）
        String context = ChatMessageSerializer.messagesToJson(list);
        String key = COMPRESSED_KEY_PREFIX + memoryId;
        boolean compressFlag = shouldCompress(memoryId, list);

        if(compressFlag) {
            // 异步压缩
            chatCompressionService.compressAndUpdateContextAsync(memoryId, list);
        }
        stringRedisTemplate.opsForValue().set(key, context, compressFlag ? Duration.ofMinutes(30) : Duration.ofHours(CONTEXT_EXPIRE));

    }

    @Override
    public void deleteMessages(Object o) {
        String memoryId = o.toString();
        // 清理 MySQL
        chatMessageMapper.deleteByMemoryId(memoryId);
        // 清理 Redis
        String key = COMPRESSED_KEY_PREFIX + memoryId;
        String timeKey = COMPRESSION_TIME_KEY_PREFIX + memoryId;
        stringRedisTemplate.delete(key);
        stringRedisTemplate.delete(timeKey);
    }

    /**
     * 从 MySQL 重建完整对话（先返回全量，异步压缩）
     */
    private List<ChatMessage> rebuildAndCompressFromDatabase(String memoryId) {
        // 1. 从 MySQL 获取所有原始消息
        List<ChatMessage> allMessages = loadAllMessagesFromDatabase(memoryId);

        // 2. 如果消息不多，直接返回
        if (allMessages.size() <= COMPRESS_THRESHOLD) {
            return allMessages;
        }

        // 3. 异步压缩并更新 Redis（不阻塞当前请求）
        log.info("消息数量{}超过阈值{}，启动异步压缩，先返回全量消息", allMessages.size(), COMPRESS_THRESHOLD);
        chatCompressionService.compressAndUpdateContextAsync(memoryId, allMessages);

        // 4. 立即返回全量消息，不等待压缩完成
        return allMessages;
    }

    /**
     * 从数据库加载所有消息
     */
    private List<ChatMessage> loadAllMessagesFromDatabase(String memoryId) {
        List<ChatMessage> messages = new ArrayList<>();
        List<ChatMessageDO> chatMessageDOS = chatMessageMapper.selectByMemoryId(memoryId);

        for (ChatMessageDO chatMessageDO : chatMessageDOS) {
            String role = chatMessageDO.getRole();
            // 优先使用增强内容，如果没有则使用原始内容
            String content = StringUtils.hasText(chatMessageDO.getEnhancedContent())
                ? chatMessageDO.getEnhancedContent()
                : chatMessageDO.getContent();

            ChatMessage message = switch (role.toLowerCase()) {
                case "system" -> SystemMessage.from(content);
                case "user" -> UserMessage.from(content);
                case "assistant" -> AiMessage.from(content);
                case "tool" -> parseToolMessage(content);
                default -> throw new RuntimeException("不存在该角色: " + role.toLowerCase());
            };
            messages.add(message);
        }
        return messages;
    }

    private String getRoleFromMessage(ChatMessage message) {
        if (message instanceof SystemMessage) {
            return "system";
        } else if (message instanceof UserMessage) {
            return "user";
        } else if (message instanceof AiMessage) {
            return "assistant";
        }else if (message instanceof ToolExecutionResultMessage) {
            return "tool";
        } else if (message instanceof CustomMessage) {
            return "custom";
        }
        throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
    }

    private String getContentMessage(ChatMessage message) {
        if (message instanceof SystemMessage) {
            return ((SystemMessage) message).text();
        } else if (message instanceof UserMessage) {

            return ((UserMessage) message).singleText();
        } else if (message instanceof AiMessage) {
            return ((AiMessage) message).text();
        }else if (message instanceof ToolExecutionResultMessage) {
            // todo 适配function call
            ToolExecutionResultMessage toolMsg = (ToolExecutionResultMessage) message;
            return String.format("{id: %s, tool_name: %s, execution_result: %s}",
                    toolMsg.id(), toolMsg.toolName(), toolMsg.text());
        } else if (message instanceof CustomMessage) {
            // 自定义消息可能需要JSON序列化
            return ((CustomMessage) message).toString();
        }
        throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
    }

    private ToolExecutionResultMessage parseToolMessage(String content) {
        // 简单实现 - 实际应根据存储格式调整
        try {
            JSONObject json = new JSONObject(Boolean.parseBoolean(content.replace("{", "{\"").replace(":", "\":\"")));
            return new ToolExecutionResultMessage(
                json.getString("message_id"),
                json.getString("tool_name"),
                json.getString("execution_result")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从增强内容中提取原始用户问题 todo 更优雅的方式
     */
    private String extractOriginalContent(String enhancedContent) {
        if (!isUserMessageEnhanced(enhancedContent)) {
            return enhancedContent;
        }

        // 增强内容的格式通常是：
        // 原始问题
        // \n文档/文件/附件的内容如下，你可以基于下面的内容回答:\n
        // RAG检索到的内容...

        String separator = "\n文档/文件/附件的内容如下，你可以基于下面的内容回答:\n";
        int separatorIndex = enhancedContent.indexOf(separator);

        if (separatorIndex > 0) {
            return enhancedContent.substring(0, separatorIndex).trim();
        }

        // 如果分隔符不存在，返回原内容
        return enhancedContent;
    }

    private static boolean isUserMessageEnhanced(String userMessage) {
        return userMessage.contains("\n文档/文件/附件的内容如下，你可以基于下面的内容回答:\n");
    }

    private int calculateToken(String text) {
        return tokenizer.encodeOrdinary(text).size();
    }

    private boolean shouldCompress(String memoryId, List<ChatMessage> chatMessages) {
        // 总Token超过门限
        int totalTokens = chatMessages.stream()
            .mapToInt(msg -> calculateToken(getContentMessage(msg)))
            .sum();
        if(totalTokens >= MAX_TOKEN_THRESHOLD) return true;

        // 1. 基本条件：超过上下文轮数设置
        if (chatMessages.size() < COMPRESS_THRESHOLD) return false;
        // 2. 防抖：避免短时间频繁压缩
        String key = COMPRESSION_TIME_KEY_PREFIX + memoryId;
        String lastTime = stringRedisTemplate.opsForValue().get(key);
        if (lastTime != null) {
            long timeDiff = System.currentTimeMillis() - Long.parseLong(lastTime);
            // 转换为毫秒
            return timeDiff >= (long) COMPRESS_MIN_INTERVAL_MINUTES * 60 * 1000;
        }

        return true;
    }

    @PostConstruct
    public void init() {
        COMPRESS_THRESHOLD = this.CONTEXT_WINDOW_SIZE * 2;
    }
}
