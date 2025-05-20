package com.iohw.knobot.libary.service;

import com.iohw.knobot.libary.domain.entity.KnowledgeLibDO;
import com.iohw.knobot.libary.domain.vo.request.CreateKnowledgeLibCommand;
import com.iohw.knobot.libary.domain.vo.request.DeleteKnowledgeLibCommand;
import com.iohw.knobot.libary.domain.vo.request.QueryLibraryDetailListRequest;
import com.iohw.knobot.libary.domain.vo.request.QueryLibraryListRequest;
import com.iohw.knobot.libary.domain.vo.request.UpdateKnowledgeLibCommand;
import com.iohw.knobot.libary.domain.vo.response.KnowledgeLibNameResponse;
import com.iohw.knobot.libary.domain.vo.response.KnowledgeLibResponse;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/25 21:46
 * @description: 知识库服务接口
 */
public interface IKnowledgeLibService {
    /**
     * 创建知识库
     */
    void createKnowledgeLib(CreateKnowledgeLibCommand command);

    /**
     * 获取知识库信息
     */
    KnowledgeLibDO getKnowledgeLib(String knowledgeLibId);

    /**
     * 获取所有知识库列表
     */
    List<KnowledgeLibResponse> queryLibraryDetailList(QueryLibraryDetailListRequest request);

    /**
     * 更新知识库信息
     */
    void updateKnowledgeLib(UpdateKnowledgeLibCommand command);

    /**
     * 更新文档数量
     */
    void updateDocumentCount(String knowledgeLibId, Integer count);

    /**
     * 删除知识库
     */
    void deleteKnowledgeLib(DeleteKnowledgeLibCommand command);

    List<KnowledgeLibNameResponse> queryKnowledgeLibList(QueryLibraryListRequest request);
}