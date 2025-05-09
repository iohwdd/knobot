package com.iohw.knobot.libary.domain.vo.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/26 13:43
 * @description:
 */
@Data
public class UpdateKnowledgeLibCommand {
    private String knowledgeLibId;
    private String knowledgeLibName;
    private String knowledgeLibDesc;
}
