package com.iohw.knobot.libary.domain.vo.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/27 21:10
 * @description:
 */
@Data
public class DeleteKnowledgeLibDocCommand {
    private String knowledgeLibId;
    private String documentId;
}
