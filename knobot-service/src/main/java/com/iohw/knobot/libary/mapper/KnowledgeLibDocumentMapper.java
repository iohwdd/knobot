package com.iohw.knobot.libary.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.iohw.knobot.libary.domain.entity.KnowledgeLibDocumentDO;

/**
 * @author: iohw
 * @date: 2025/4/25 21:46
 * @description: 知识库文档数据访问层
 */
@Mapper
public interface KnowledgeLibDocumentMapper {
    /**
     * 插入文档
     */
    void insert(KnowledgeLibDocumentDO document);

    /**
     * 批量插入文档
     */
    void batchInsert(@Param("documents") List<KnowledgeLibDocumentDO> documents);

    /**
     * 根据知识库ID和文档ID查询文档
     */
    KnowledgeLibDocumentDO selectById(@Param("knowledgeLibId") String knowledgeLibId,
                                    @Param("documentId") String documentId);

    /**
     * 查询知识库下的所有文档
     */
    List<KnowledgeLibDocumentDO> selectListByKnowledgeLibId(@Param("knowledgeLibId") String knowledgeLibId);

    /**
     * 更新文档信息
     */
    void update(KnowledgeLibDocumentDO document);

    /**
     * 更新文档状态
     */
    void updateStatus(@Param("knowledgeLibId") String knowledgeLibId,
                     @Param("documentId") String documentId,
                     @Param("status") Integer status);

    /**
     * 删除文档
     */
    void deleteById(@Param("documentId") String documentId);

    /**
     * 批量删除文档
     */
    void batchDelete(@Param("knowledgeLibId") String knowledgeLibId,
                    @Param("documentIds") List<String> documentIds);
    void deleteByKnowledgeLibId(@Param("knowledgeLibId") String knowledgeLibId);
    /**
     * 统计知识库下的文档数量
     */
    int selectCountByKnowledgeLibId(@Param("knowledgeLibId") String knowledgeLibId);
}