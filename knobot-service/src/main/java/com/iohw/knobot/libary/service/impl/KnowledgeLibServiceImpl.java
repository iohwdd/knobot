package com.iohw.knobot.libary.service.impl;

import com.iohw.knobot.libary.mapper.KnowledgeLibDocumentMapper;
import com.iohw.knobot.library.model.KnowledgeLibDO;
import com.iohw.knobot.libary.mapper.KnowledgeLibMapper;
import com.iohw.knobot.libary.service.KnowledgeLibService;
import com.iohw.knobot.library.model.convert.KnowledgeLibConvert;
import com.iohw.knobot.library.model.vo.KnowledgeLibNameVO;
import com.iohw.knobot.library.model.vo.KnowledgeLibVO;
import com.iohw.knobot.library.request.*;
import com.iohw.knobot.utils.IdGeneratorUtil;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * @author: iohw
 * @date: 2025/4/25 21:46
 * @description: 知识库服务实现类
 */
@Service
@RequiredArgsConstructor
public class KnowledgeLibServiceImpl implements KnowledgeLibService {
    private final KnowledgeLibDocumentMapper documentMapper;
    private final KnowledgeLibMapper knowledgeLibMapper;

    @Override
    public void createKnowledgeLib(CreateKnowledgeLibCommand command) {
        KnowledgeLibDO knowledgeLibDO = new KnowledgeLibDO();
        knowledgeLibDO.setUserId(command.getUserId());
        knowledgeLibDO.setKnowledgeLibId(IdGeneratorUtil.generateLibId());
        knowledgeLibDO.setKnowledgeLibName(command.getKnowledgeLibName());
        knowledgeLibDO.setKnowledgeLibDesc(command.getKnowledgeLibDesc());
        knowledgeLibDO.setDocumentCount(0);
        knowledgeLibMapper.insert(knowledgeLibDO);
    }

    @Override
    public KnowledgeLibDO getKnowledgeLib(String knowledgeLibId) {
        return knowledgeLibMapper.selectById(knowledgeLibId);
    }

    @Override
    public List<KnowledgeLibVO> queryLibraryDetailList(QueryLibraryDetailListRequest request) {
        List<KnowledgeLibDO> knowledgeLibDOS = knowledgeLibMapper.selectByUserId(request.getUserId());
        if(knowledgeLibDOS.isEmpty()) return null;
        return KnowledgeLibConvert.INSTANCE.toVOList(knowledgeLibDOS);
    }

    @Override
    public void updateKnowledgeLib(UpdateKnowledgeLibCommand command) {
        KnowledgeLibDO knowledgeLibDO = new KnowledgeLibDO();
        knowledgeLibDO.setKnowledgeLibId(command.getKnowledgeLibId());
        knowledgeLibDO.setKnowledgeLibName(command.getKnowledgeLibName());
        knowledgeLibDO.setKnowledgeLibDesc(command.getKnowledgeLibDesc());
        knowledgeLibMapper.update(knowledgeLibDO);
    }

    @Override
    public void updateDocumentCount(String knowledgeLibId, Integer count) {
        knowledgeLibMapper.updateDocumentCount(knowledgeLibId, count);
    }

    final EmbeddingStore<TextSegment> embeddingStore;
    @Override
    public void deleteKnowledgeLib(DeleteKnowledgeLibCommand command) {
        // 1. 删除知识库关联的文档
        documentMapper.deleteByKnowledgeLibId(command.getKnowledgeLibId());
        // 2. 删除知识库
        knowledgeLibMapper.deleteById(command.getKnowledgeLibId());
        // 3. 删除向量数据库中的数据 - 根据元数据 knowledgeLibId 来删
        embeddingStore.removeAll(metadataKey("knowledgeLibId").isEqualTo(command.getKnowledgeLibId()));
    }

    @Override
    public List<KnowledgeLibNameVO> queryKnowledgeLibList(QueryLibraryListRequest request) {
        List<KnowledgeLibDO> list = knowledgeLibMapper.selectByUserId(request.getUserId());
        return list.stream()
                .map(item -> KnowledgeLibNameVO.builder()
                        .knowledgeLibName(item.getKnowledgeLibName())
                        .knowledgeLibId(item.getKnowledgeLibId())
                        .build())
                .toList();
    }


}