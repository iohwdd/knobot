package com.iohw.knobot.chat.ai.config;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.iohw.knobot.chat.domain.entity.ChatMessageDO;
import com.iohw.knobot.chat.mapper.ChatMessageMapper;
import com.iohw.knobot.utils.IdGeneratorUtil;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author: iohw
 * @date: 2025/4/13 10:35
 * @description:
 */
@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private Tokenizer tokenizer;

    private Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(100)
            .build();

    @Override
    public List<ChatMessage> getMessages(Object o) {
        String memoryId = (String) o;
        String json = cache.getIfPresent(memoryId);
        if(StringUtils.hasText(json))
            // 走缓存
            return ChatMessageDeserializer.messagesFromJson(json);

        List<ChatMessage> messages = new ArrayList<>();
        List<ChatMessageDO> chatMessageDOS = chatMessageMapper.selectByMemoryId(memoryId);
        for (ChatMessageDO chatMessageDO : chatMessageDOS) {
            String role = chatMessageDO.getRole();
            String content = chatMessageDO.getEnhancedContent() != null ? chatMessageDO.getEnhancedContent() : chatMessageDO.getContent();
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

    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {
        String json = ChatMessageSerializer.messagesToJson(list);
        cache.put(o.toString(), json);
        // todo 优化：增量更新 or ...
        // 全量清空旧数据 + 全量增加新数据
        String memoryId = o.toString();
        deleteMessages(memoryId);
        List<ChatMessageDO> messageList = new ArrayList<>();
        for (ChatMessage chatMessage : list) {
            String role = getRoleFromMessage(chatMessage);
            String content = getContentMessage(chatMessage);
            // 若经过rag增强，分离出用户原始输入信息与被增加的输入信息
            String originContent = content;
            String enhancedContent = null;
            if(isUserMessageEnhanced(content)) {
                enhancedContent = content;
                originContent = content.substring(0, content.lastIndexOf("\n文档/文件/附件的内容如下，你可以基于下面的内容回答:\n"));
            }
            //int tokens = tokenizer.estimateTokenCountInMessage(chatMessage);
            ChatMessageDO chatHistoryDO = ChatMessageDO.builder()
                    .role(role)
                    .content(originContent)
                    .enhancedContent(enhancedContent)
                    .memoryId(memoryId)
                    .messageId(IdGeneratorUtil.generateId())
                    //.tokens(tokens)
                    .build();
            messageList.add(chatHistoryDO);
        }
        chatMessageMapper.batchInsert(messageList);
    }

    @Override
    public void deleteMessages(Object o) {
        String memoryId = o.toString();
        chatMessageMapper.deleteByMemoryId(memoryId);
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
            // 工具执行结果需要特殊处理
            ToolExecutionResultMessage toolMsg = (ToolExecutionResultMessage) message;
            return String.format("{id: %s, tool_name: %s, execution_result: %s}",
                    toolMsg.id(), toolMsg.toolName(), toolMsg.text());
        } else if (message instanceof CustomMessage) {
            // 自定义消息可能需要JSON序列化
            return ((CustomMessage) message).toString();
        }
        throw new IllegalArgumentException("Unknown message type: " + message.getClass().getName());
    }
    private static boolean isUserMessageEnhanced(String userMessage) {
        return userMessage.contains("\n文档/文件/附件的内容如下，你可以基于下面的内容回答:\n");
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
    }}
