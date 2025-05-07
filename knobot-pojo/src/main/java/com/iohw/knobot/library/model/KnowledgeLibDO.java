package com.iohw.knobot.library.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

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