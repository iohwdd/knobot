package com.iohw.knobot.libary.service.impl;

import com.iohw.knobot.libary.service.IKnowledgeLibDocumentService;
import com.iohw.knobot.common.dto.FileUploadDTO;
import com.iohw.knobot.libary.domain.convert.KnowledgeLibDocumentConvert;
import com.iohw.knobot.libary.domain.entity.KnowledgeLibDocumentDO;
import com.iohw.knobot.libary.domain.vo.request.CreateKnowledgeLibDocCommand;
import com.iohw.knobot.libary.domain.vo.request.DeleteKnowledgeLibDocCommand;
import com.iohw.knobot.libary.domain.vo.request.UpdateKnowledgeLibDocCommand;
import com.iohw.knobot.libary.mapper.KnowledgeLibDocumentMapper;
import com.iohw.knobot.libary.service.IKnowledgeLibService;
import com.iohw.knobot.libary.domain.vo.response.KnowledgeLibDocumentResponse;
import com.iohw.knobot.upload.FileUploadFactory;
import com.iohw.knobot.upload.LocalUploadFileStrategy;
import com.iohw.knobot.utils.FileUtils;
import com.iohw.knobot.utils.IdGeneratorUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

/**
 * @author: iohw
 * @date: 2025/4/25 21:46
 * @description: 知识库文档服务实现类
 */
@Service
@RequiredArgsConstructor
public class KnowledgeLibDocumentServiceImpl implements IKnowledgeLibDocumentService {
    private final KnowledgeLibDocumentMapper documentMapper;
    private final IKnowledgeLibService IKnowledgeLibService;
    private final EmbeddingStoreIngestor ingestor;
    private final FileUploadFactory fileUploadFactory;
    private final KnowledgeLibDocumentConvert documentConvert;

    @Override
    public void addDocument(CreateKnowledgeLibDocCommand command) {
        KnowledgeLibDocumentDO documentDO = new KnowledgeLibDocumentDO();
        documentDO.setDocumentName(command.getDocumentName());
        documentDO.setDocumentDesc(command.getDocumentDesc());
        documentDO.setDocumentId(IdGeneratorUtil.generateDocId());
        documentDO.setKnowledgeLibId(command.getKnowledgeLibId());
        FileUploadDTO upload = fileUploadFactory.getUploadStrategy().upload(command.getFile(), "doc");

        //todo 上传到本地，导入向量数据库后删除 - 向量数据库加载貌似必须从本地文件中导入，后面看看有没有解决方法
        LocalUploadFileStrategy fileStrategy = new LocalUploadFileStrategy();
        FileUploadDTO dto = fileStrategy.upload(command.getFile(), "/tmp");

        documentDO.setPath(upload.getFilePath());
        documentDO.setDocumentSize(FileUtils.getFileSizeInMB(command.getFile()));
        documentDO.setUrl(upload.getFileUrl());

        documentMapper.insert(documentDO);

        //更新向量数据库
        loadFile2Store(dto.getFilePath());

        // 更新文档数量
        updateKnowledgeLibDocumentCount(documentDO.getKnowledgeLibId());
    }

    private void loadFile2Store(String filePath) {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        DocumentParser parser = new ApacheTikaDocumentParser();
        Document document = loadDocument(path.toString(), parser);
        // 删除临时文件
        FileUtils.deleteFile(filePath);
        ingestor.ingest(document);
    }

    @Override
    public void batchAddDocuments(List<KnowledgeLibDocumentDO> documents) {
        if (!documents.isEmpty()) {
            documentMapper.batchInsert(documents);
            updateKnowledgeLibDocumentCount(documents.get(0).getKnowledgeLibId());
        }
    }

    @Override
    public KnowledgeLibDocumentDO queryDocument(String knowledgeLibId, String documentId) {
        return documentMapper.selectById(knowledgeLibId, documentId);
    }

    @Override
    public List<KnowledgeLibDocumentResponse> queryDocumentList(String knowledgeLibId) {
        return documentConvert.toVO(documentMapper.selectListByKnowledgeLibId(knowledgeLibId));
    }

    @Override
    public void updateDocument(UpdateKnowledgeLibDocCommand command) {
        KnowledgeLibDocumentDO documentDO = new KnowledgeLibDocumentDO();
        documentDO.setDocumentName(command.getDocumentName());
        documentDO.setDocumentDesc(command.getDocumentDesc());
        documentDO.setDocumentId(command.getDocumentId());
        if(command.getFile() != null) {
            FileUploadDTO upload = fileUploadFactory.getUploadStrategy().upload(command.getFile(), "doc");
            documentDO.setPath(upload.getFilePath());
            documentDO.setDocumentSize(FileUtils.getFileSizeInMB(command.getFile()));
        }
        documentMapper.update(documentDO);
    }

    @Override

    public void updateDocumentStatus(String knowledgeLibId, String documentId, Integer status) {
        documentMapper.updateStatus(knowledgeLibId, documentId, status);
    }

    @Override

    public void deleteDocument(DeleteKnowledgeLibDocCommand command) {
        documentMapper.deleteById(command.getDocumentId());
        updateKnowledgeLibDocumentCount(command.getKnowledgeLibId());
    }

    @Override

    public void batchDeleteDocuments(String knowledgeLibId, List<String> documentIds) {
        documentMapper.batchDelete(knowledgeLibId, documentIds);
        updateKnowledgeLibDocumentCount(knowledgeLibId);
    }

    @Override
    public int queryDocumentCount(String knowledgeLibId) {
        return documentMapper.selectCountByKnowledgeLibId(knowledgeLibId);
    }

    /**
     * 更新知识库的文档数量
     */
    private void updateKnowledgeLibDocumentCount(String knowledgeLibId) {
        int count = queryDocumentCount(knowledgeLibId);
        IKnowledgeLibService.updateDocumentCount(knowledgeLibId, count);
    }
}