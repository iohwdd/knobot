package com.iohw.knobot.chat.service.impl;

import com.iohw.knobot.chat.ai.assistant.AssistantService;
import com.iohw.knobot.chat.ai.assistant.IAssistant.StreamingAssistant;
import com.iohw.knobot.chat.ai.assistant.IAssistant.SummarizeAssistant;
import com.iohw.knobot.chat.ai.assistant.IAssistant.WebSearchAssistant;
import com.iohw.knobot.chat.domain.convert.ChatConversationConverter;
import com.iohw.knobot.chat.domain.convert.ChatMessageConverter;
import com.iohw.knobot.chat.domain.entity.ChatMessageDO;
import com.iohw.knobot.chat.domain.vo.request.ChatRequest;
import com.iohw.knobot.chat.domain.vo.request.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.domain.vo.response.ChatMessageResponse;
import com.iohw.knobot.chat.domain.vo.response.FileUploadResponse;
import com.iohw.knobot.chat.mapper.ChatMessageMapper;
import com.iohw.knobot.chat.service.ChatService;
import com.iohw.knobot.chat.service.ConversationSideBarService;
import com.iohw.knobot.common.Result;
import com.iohw.knobot.upload.LocalUploadFileStrategy;
import com.iohw.knobot.upload.UploadFileStrategy;
import com.iohw.knobot.upload.dto.FileUploadDTO;
import com.iohw.knobot.utils.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

/**
 * @author: iohw
 * @date: 2025/4/13 18:16
 * @description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageConverter chatMessageConverter;
    private final AssistantService assistantService;
    private final SummarizeAssistant summarizeAssistant;
    private final WebSearchAssistant webSearchAssistant;
    private final ConversationSideBarService conversationSideBarService;
    private final EmbeddingStoreIngestor ingestor;

    private final Map<String, String> filePathMap = new HashMap<>();

    @Override
    public List<ChatMessageResponse> queryHistoryMessages(String memoryId) {
        List<ChatMessageDO> chatMessageDOS = chatMessageMapper.selectByMemoryId(memoryId);
        return chatMessageConverter.toDtoList(chatMessageDOS);
    }

    @Override
    public boolean isFirstQuestion(String memoryId) {
        // 查询该会话下用户发送的消息数量
        List<ChatMessageDO> userMessages = chatMessageMapper.selectByMemoryIdAndRole(memoryId, "user");
        return userMessages.isEmpty();
    }

    @Override
    public FileUploadResponse uploadFile4Chat(MultipartFile file) {
        UploadFileStrategy uploadStrategy = new LocalUploadFileStrategy();
        FileUploadDTO uploadDto = uploadStrategy.upload(file, "/tmp");
        filePathMap.put(uploadDto.getFileId(), uploadDto.getFilePath());
        return FileUploadResponse.builder()
            .fileId(uploadDto.getFileId())
            .fileName(uploadDto.getFileName())
            .filePath(uploadDto.getFilePath())
            .build();
    }

    @Override
    public SseEmitter chat(String memoryId, ChatRequest request) {
        SseEmitter emitter = new SseEmitter(-1L); // 无超时
        String fileId = request.getFileId();

        // 判断是否是首次提问 - 更新标题
        boolean isFirstQuestion = this.isFirstQuestion(memoryId);
        if (isFirstQuestion) {
            log.info("用户 {} 在会话 {} 中首次提问", request.getUserId(), memoryId);
            String newTitle = summarizeAssistant.summarize(request.getUserMessage());
            conversationSideBarService.updateChatConversationTitle(
                UpdateConversationTitleCommand.builder()
                    .memoryId(memoryId)
                    .newTitle(newTitle)
                    .build()
            );
        }

        //上传了附件
        if(!StringUtils.isEmpty(fileId)) {
            String filePath = filePathMap.get(fileId);
            loadFile2Store(filePath);
        }

        try {
            StreamingAssistant assistant = assistantService.getRagAssistant(memoryId, request.getKnowledgeLibId());
            // 开启联网搜索
            if(request.getIsWebSearchRequest()) {
                assistant = webSearchAssistant;
            }
            TokenStream tokenStream = assistant.chat(memoryId, request.getUserMessage());
            tokenStream
                .onPartialResponse(token -> {
                    try {
                        emitter.send(SseEmitter.event()
                            .data(token)
                            .id(String.valueOf(System.currentTimeMillis()))
                            .name("message"));
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse(response -> {
                    try {
                        emitter.send(SseEmitter.event()
                            .data("[DONE]")
                            .id("done")
                            .name("done"));
                        emitter.complete();
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onError(e -> emitter.completeWithError(e))
                .start();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    private void loadFile2Store(String filePath) {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        DocumentParser parser = new ApacheTikaDocumentParser();
        Document document = loadDocument(path.toString(), parser);
        // 删除临时文件
        FileUtils.deleteFile(filePath);
        ingestor.ingest(document);
    }
}
