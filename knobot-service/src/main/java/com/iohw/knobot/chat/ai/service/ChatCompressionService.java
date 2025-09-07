package com.iohw.knobot.chat.ai.service;

import com.iohw.knobot.chat.ai.assistant.IAssistant.SummarizeAssistant;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * @author: iohw
 * @date: 2025/9/7
 * @description: 对话压缩异步服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatCompressionService {

    private static final String COMPRESSED_KEY_PREFIX = "chat:compressed:";
    private static final String COMPRESSION_TIME_KEY_PREFIX = "chat:compressed:last_time:";

    private final StringRedisTemplate stringRedisTemplate;
    private final ApplicationContext applicationContext;

    @Value("${chat.context.window-size}")
    private int CONTEXT_WINDOW_SIZE;

    @Value("${chat.context.expire-hours}")
    private int CONTEXT_EXPIRE;

    /**
     * 异步压缩并更新 Redis 中的对话上下文
     */
    @Async("chatCompressionExecutor")
    public void compressAndUpdateContextAsync(String memoryId, List<ChatMessage> messages) {
        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();

        log.info("开始异步压缩，memoryId: {}, 线程: {}, 消息数: {}", memoryId, threadName, messages.size());

        try {
            String context = ChatMessageSerializer.messagesToJson(messages);
            // 压缩当前消息列表
            List<ChatMessage> compressedMessages = compressMessages(context);
            String compressedJson = ChatMessageSerializer.messagesToJson(compressedMessages);

            // 保存到 Redis
            String key = COMPRESSED_KEY_PREFIX + memoryId;
            stringRedisTemplate.opsForValue().set(key, compressedJson,
                Duration.ofHours(CONTEXT_EXPIRE));

            String timeKey = COMPRESSION_TIME_KEY_PREFIX + memoryId;
            stringRedisTemplate.opsForValue().set(timeKey, System.currentTimeMillis() + "",
                Duration.ofHours(24)); // 24小时过期，比压缩间隔长

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("异步压缩完成，memoryId: {}, 线程: {}, 原始{}条 -> 压缩{}条, 总耗时{}ms",
                memoryId, threadName, messages.size(), compressedMessages.size(), totalTime);
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            log.error("异步压缩失败，memoryId: {}, 线程: {}, 耗时{}ms, 错误: {}",
                memoryId, threadName, totalTime, e.getMessage(), e);
            // 不影响主流程，继续执行
        }
    }

    /**
     * 压缩对话消息
     */
    private List<ChatMessage> compressMessages(String context) {
        long startTime = System.currentTimeMillis();

        // 先反序列化一次，避免重复操作
        List<ChatMessage> originalMessages = ChatMessageDeserializer.messagesFromJson(context);
        int originalSize = originalMessages.size();

        try {
            SummarizeAssistant summarizeAssistant = getSummarizeAssistant();
            String summarizedContext = summarizeAssistant.multiQuerySummarize(context, CONTEXT_WINDOW_SIZE);
            List<ChatMessage> result = ChatMessageDeserializer.messagesFromJson(summarizedContext);

            // 记录压缩指标
            long compressionTime = System.currentTimeMillis() - startTime;
            double compressionRatio = (double) result.size() / originalSize;
            log.info("AI压缩成功：原始{}条 -> 压缩{}条，压缩比{:.2f}，耗时{}ms",
                originalSize, result.size(), compressionRatio, compressionTime);

            return result;
        } catch (Exception e) {
            log.warn("AI压缩失败，使用简单截取策略：{}", e.getMessage());
            // 降级策略：使用已经反序列化的消息，避免重复操作
            List<ChatMessage> truncatedResult = simpleMessageTruncation(originalMessages);
            log.info("降级截取完成：原始{}条 -> 截取{}条", originalSize, truncatedResult.size());
            return truncatedResult;
        }
    }

    /**
     * 简单的消息截取策略（降级方案）
     */
    private List<ChatMessage> simpleMessageTruncation(List<ChatMessage> messages) {
        if (messages.size() <= CONTEXT_WINDOW_SIZE * 2) {
            return messages;
        }

        // 保留最近的消息
        int keepCount = CONTEXT_WINDOW_SIZE * 2; // 保留最近的消息
        int startIndex = Math.max(0, messages.size() - keepCount);

        return messages.subList(startIndex, messages.size());
    }

    /**
     * 延迟获取 SummarizeAssistant，避免循环依赖
     */
    private SummarizeAssistant getSummarizeAssistant() {
        return applicationContext.getBean(SummarizeAssistant.class);
    }
}
