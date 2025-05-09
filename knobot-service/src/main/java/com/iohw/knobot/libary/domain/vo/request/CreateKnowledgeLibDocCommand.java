package com.iohw.knobot.libary.domain.vo.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


/**
 * @author: iohw
 * @date: 2025/4/26 9:12
 * @description:
 */
@Data
public class CreateKnowledgeLibDocCommand {
    private String knowledgeLibId;
    private String documentName;
    private String documentDesc;
    private MultipartFile file;
}
