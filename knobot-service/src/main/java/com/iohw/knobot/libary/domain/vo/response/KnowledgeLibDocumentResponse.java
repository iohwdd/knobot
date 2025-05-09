package com.iohw.knobot.libary.domain.vo.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: iohw
 * @date: 2025/4/25 22:39
 * @description:
 */
@Data
@Builder
public class KnowledgeLibDocumentResponse {
    private String documentId;
    private String documentName;
    private String documentDesc;
    private Double documentSize;
    private int status;
    private LocalDateTime uploadTime;
}
