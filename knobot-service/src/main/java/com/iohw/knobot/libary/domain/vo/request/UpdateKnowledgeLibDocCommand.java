package com.iohw.knobot.libary.domain.vo.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/26 16:44
 * @description:
 */
@Data
public class UpdateKnowledgeLibDocCommand {
    private String documentId;
    private String documentName;
    private String documentDesc;
    private MultipartFile file;
}
