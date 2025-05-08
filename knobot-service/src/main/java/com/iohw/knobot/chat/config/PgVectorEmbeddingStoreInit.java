package com.iohw.knobot.chat.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: iohw
 * @date: 2025/4/16 22:24
 * @description:
 */
@Configuration
@RequiredArgsConstructor
public class PgVectorEmbeddingStoreInit {
    final PgVectorProperties pgVectorProperties;

    @Bean
    EmbeddingStore<TextSegment> initEmbeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(pgVectorProperties.getHost())
                .port(pgVectorProperties.getPort())
                .user(pgVectorProperties.getUser())
                .password(pgVectorProperties.getPassword())
                .database(pgVectorProperties.getDatabase())
                .table(pgVectorProperties.getTable())
                .dimension(1024)
                .dropTableFirst(false)
                .createTable(true)
                .build();

    }
}
