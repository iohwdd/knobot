package com.iohw.knobot.libary.domain.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/25 21:46
 * @description: 知识库文档实体类
 */
@Data
public class KnowledgeLibDocumentDO {
    /**
     * 文档编号
     */
    private String documentId;

    /**
     * 关联知识库编号
     */
    private String knowledgeLibId;
    /**
     * 文档名称
     */
    private String documentName;
    /**
     * 文档描述
     */
    private String documentDesc;

    /**
     * 文档大小
     */
    private Double documentSize;

    /**
     * 文档存储url
     */
    private String url;

    /**
     * 文档存储本地路径
     */
    private String path;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
}