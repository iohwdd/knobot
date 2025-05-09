package com.iohw.knobot.chat.ai.assistant.IAssistant.base;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * @author: iohw
 * @date: 2025/4/12 20:54
 * @description:
 */
public interface StreamAssistant {
    String chat(String userMessage);

    TokenStream chat(@MemoryId String memoryId, @UserMessage String input);

    Flux<String> Fchat(@MemoryId String memoryId, @UserMessage String input);
}
