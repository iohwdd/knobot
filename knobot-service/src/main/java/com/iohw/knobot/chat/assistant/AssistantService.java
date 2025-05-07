package com.iohw.knobot.chat.assistant;

import com.iohw.knobot.chat.assistant.IAssistant.RAGAssistant;
import com.iohw.knobot.chat.assistant.IAssistant.SummarizeAssistant;
import com.iohw.knobot.chat.assistant.IAssistant.WebSearchAssistant;
import com.iohw.knobot.chat.config.PersistentChatMemoryStore;
import com.iohw.knobot.chat.config.ContentRetrieverFactory;
import com.iohw.knobot.chat.tool.SendEmailTool;
import com.iohw.knobot.config.properties.WebSearchProperties;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: iohw
 * @date: 2025/4/16 21:37
 * @description:
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AssistantService {
    private final ChatLanguageModel chatLanguageModel;

    private final StreamingChatLanguageModel streamingChatLanguageModel;

    private final PersistentChatMemoryStore chatMemoryStore;

    private final ContentRetrieverFactory contentRetrieverFactory;

    final WebSearchProperties webSearchProperties;

    final SendEmailTool emailTool;

    // 缓存已创建的 RAG 助手实例
    private final Map<String, RAGAssistant> ragAssistantCache = new ConcurrentHashMap<>();

    public RAGAssistant getRagAssistant(String memoryId, String knowledgeLibId) {
        if(ragAssistantCache.containsKey(memoryId)) {
            return ragAssistantCache.get(memoryId);
        }
        return createRagAssistant(memoryId, knowledgeLibId);
    }

    private RAGAssistant createRagAssistant(String memoryId, String knowledgeLibId) {
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetrieverFactory.createRetriever(memoryId, knowledgeLibId))
                .contentInjector(DefaultContentInjector.builder()
                        .promptTemplate(PromptTemplate.from("{{userMessage}}\n文档/文件/附件的内容如下，你可以基于下面的内容回答:\n{{contents}}"))
                        .build())
                .build();

        return AiServices.builder(RAGAssistant.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemoryProvider(id -> MessageWindowChatMemory.builder()
                        .id(id)
                        .maxMessages(20)
                        .chatMemoryStore(chatMemoryStore)
                        .build())
                .build();
    }

    @Bean
    public WebSearchAssistant webSearchAssistant() {
        WebSearchEngine searchEngine = SearchApiWebSearchEngine.builder()
                .apiKey(webSearchProperties.getApiKey())
                .engine(webSearchProperties.getEngine())
                .build();

        EmbeddingStoreContentRetriever embeddingStoreContentRetriever = contentRetrieverFactory.createRetriever(null, null);
        WebSearchContentRetriever webSearchContentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(searchEngine)
                .maxResults(3)
                .build();

        QueryRouter queryRouter = new DefaultQueryRouter(embeddingStoreContentRetriever, webSearchContentRetriever);
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .contentInjector(DefaultContentInjector.builder()
                        .promptTemplate(PromptTemplate.from("{{userMessage}}\n文档/文件/附件的内容如下，你可以基于下面的内容回答:\n{{contents}}"))
                        .build())
                .build();

        return AiServices.builder(WebSearchAssistant.class)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemoryProvider(id -> MessageWindowChatMemory.builder()
                        .id(id)
                        .maxMessages(20)
                        .chatMemoryStore(chatMemoryStore)
                        .build())
                .build();
    }

    @Bean
    public SummarizeAssistant summarizeAssistant() {
        return AiServices.builder(SummarizeAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
