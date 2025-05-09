package com.iohw.knobot.libary.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/26 17:14
 * @description:
 */
@Data
@Builder
public class KnowledgeLibNameResponse {
    private String knowledgeLibId;
    private String knowledgeLibName;
}
