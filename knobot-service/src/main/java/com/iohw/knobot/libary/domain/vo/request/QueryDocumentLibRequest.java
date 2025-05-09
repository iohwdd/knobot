package com.iohw.knobot.libary.domain.vo.request;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/25 23:15
 * @description:
 */
@Data
@Builder
public class QueryDocumentLibRequest {
    private String knowledgeLibId;
}
