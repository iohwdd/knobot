package com.iohw.knobot.chat.ai.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * @author: iohw
 * @date: 2025/4/21 22:32
 * @description:
 */
@Component
@RequiredArgsConstructor
public class ContentRetrieverFactory {
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    /**
     * 检索器 - 设置元数据检索字段
     * @param memoryId
     * @param knowledgeId
     * @return
     */ 
    public EmbeddingStoreContentRetriever createRetriever(String memoryId, String knowledgeId) {
        if(!StringUtils.hasText(memoryId)) {
            return EmbeddingStoreContentRetriever.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .maxResults(5)
                    .minScore(0.8)
                    .build();
        }
        if(!StringUtils.hasText(knowledgeId)) {
            return EmbeddingStoreContentRetriever.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .filter(
                            metadataKey("memoryId").isEqualTo(memoryId)
                    )
                    .maxResults(5)
                    .minScore(0.8)
                    .build();
        }
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .filter(
                        metadataKey("memoryId").isEqualTo(memoryId).or(
                                metadataKey("knowledgeLibId").isEqualTo(knowledgeId)
                        )
                )
                .maxResults(5)
                .minScore(0.8)
                .build();
    }
}
