package com.iohw.knobot.chat.ai.config;

import com.iohw.knobot.utils.ThreadLocalUtils;
import dev.langchain4j.community.model.dashscope.QwenTokenizer;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author: iohw
 * @date: 2025/4/18 23:27
 * @description:
 */
@Configuration
@ConfigurationProperties(prefix = "llm")
@Data
public class AiConfig {
    private String apiKey;
    private String model;
    private String baseUrl;

    /**
     * 自主配置大模型 - 目前配置为DeepSeek R1
     *
     * @return
     */
    //@Bean
    StreamingChatLanguageModel streamingChatLanguageModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(model)
                .build();
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingStore<TextSegment> embeddingStore,
                                                         EmbeddingModel embeddingModel) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(300,20);
        return EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(documentSplitter)
                .documentTransformer(dc -> {
                    String memoryId = (String) ThreadLocalUtils.get("memoryId");
                    String knowledgeLibId = (String) ThreadLocalUtils.get("knowledgeLibId");

                    if(StringUtils.hasText(memoryId))
                        dc.metadata().put("memoryId", memoryId);
                    if(StringUtils.hasText(knowledgeLibId))
                        dc.metadata().put("knowledgeLibId", knowledgeLibId);
                    return dc;
                })
                .build();
    }
}
