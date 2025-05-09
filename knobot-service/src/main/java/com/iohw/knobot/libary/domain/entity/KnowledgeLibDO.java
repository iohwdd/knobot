package com.iohw.knobot.libary.domain.entity;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/25 21:46
 * @description: 知识库实体类
 */
@Data
public class KnowledgeLibDO {
    /**
     * 知识库编号
     */
    private String knowledgeLibId;
    /**
     * 所属用户编号
     */
    private String userId;
    /**
     * 知识库名称
     */
    private String knowledgeLibName;

    /**
     * 知识库描述
     */
    private String knowledgeLibDesc;

    /**
     * 文档数量
     */
    private Integer documentCount;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd")
    private LocalDateTime updateTime;
}