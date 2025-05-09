package com.iohw.knobot.libary.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.iohw.knobot.libary.domain.entity.KnowledgeLibDO;

/**
 * @author: iohw
 * @date: 2025/4/25 21:46
 * @description: 知识库数据访问层
 */
@Mapper
public interface KnowledgeLibMapper {
    /**
     * 创建知识库
     */
    void insert(KnowledgeLibDO knowledgeLib);

    /**
     * 根据ID查询知识库
     */
    KnowledgeLibDO selectById(@Param("knowledgeLibId") String knowledgeLibId);
    /**
     * 根据userId查询知识库
     */
    List<KnowledgeLibDO> selectByUserId(@Param("userId") String userId);
    /**
     * 查询所有知识库
     */
    List<KnowledgeLibDO> selectAll();

    /**
     * 更新知识库信息
     */
    void update(KnowledgeLibDO knowledgeLib);

    /**
     * 更新文档数量
     */
    void updateDocumentCount(@Param("knowledgeLibId") String knowledgeLibId, @Param("count") Integer count);

    /**
     * 删除知识库
     */
    void deleteById(@Param("knowledgeLibId") String knowledgeLibId);
}