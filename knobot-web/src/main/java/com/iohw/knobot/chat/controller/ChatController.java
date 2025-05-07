package com.iohw.knobot.chat.controller;

import com.iohw.knobot.chat.assistant.AssistantService;
import com.iohw.knobot.chat.assistant.IAssistant.StreamingAssistant;
import com.iohw.knobot.chat.assistant.IAssistant.SummarizeAssistant;
import com.iohw.knobot.chat.assistant.IAssistant.WebSearchAssistant;
import com.iohw.knobot.chat.model.dto.ChatSessionDto;
import com.iohw.knobot.chat.model.dto.ChatMessageDto;
import com.iohw.knobot.chat.request.ChatRequest;
import com.iohw.knobot.chat.request.command.CreateConversationCommand;
import com.iohw.knobot.chat.request.command.DeleteConversationCommand;
import com.iohw.knobot.chat.request.command.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.vo.FileUploadVO;
import com.iohw.knobot.chat.service.ChatService;
import com.iohw.knobot.chat.service.SessionSideBarService;
import com.iohw.knobot.chat.vo.ChatSessionVO;
import com.iohw.knobot.common.annotation.MdcDot;
import com.iohw.knobot.common.dto.FileUploadDto;
import com.iohw.knobot.common.response.Result;
import com.iohw.knobot.upload.FileUploadFactory;
import com.iohw.knobot.upload.LocalUploadFileStrategy;
import com.iohw.knobot.upload.UploadFileStrategy;
import com.iohw.knobot.utils.FileUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

/**
 * @author: iohw
 * @date: 2025/4/12 19:37
 * @description: 聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final SessionSideBarService sessionSideBarService;
    private final ChatService chatService;
    private final WebSearchAssistant webSearchAssistant;
    private final EmbeddingStoreIngestor ingestor;
    private final AssistantService assistantService;
    private final SummarizeAssistant summarizeAssistant;

    private static final Map<String, String> filePathMap = new HashMap<>();

    @PostMapping("/upload")
    public Result<FileUploadVO> uploadFile(@RequestParam("file") MultipartFile file) {
        UploadFileStrategy uploadStrategy = new LocalUploadFileStrategy();
        FileUploadDto uploadDto = uploadStrategy.upload(file, "/tmp");

        FileUploadVO fileUploadVO = FileUploadVO.builder()
                .fileId(uploadDto.getFileId())
                .fileName(uploadDto.getFileName())
                .filePath(uploadDto.getFilePath())
                .build();
        filePathMap.put(uploadDto.getFileId(), uploadDto.getFilePath());
        return Result.success(fileUploadVO);
    }

    @MdcDot
    @GetMapping(value = "/{memoryId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@PathVariable String memoryId, ChatRequest request) {
        SseEmitter emitter = new SseEmitter(-1L); // 无超时
        String fileId = request.getFileId();

        // 判断是否是首次提问 - 更新标题
        boolean isFirstQuestion = chatService.isFirstQuestion(memoryId);
        if (isFirstQuestion) {
            log.info("用户 {} 在会话 {} 中首次提问", request.getUserId(), memoryId);
            String newTitle = summarizeAssistant.summarize(request.getUserMessage());
            sessionSideBarService.updateChatConversationTitle(
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

    @GetMapping("/conversation-history")
    public Result<List<ChatSessionDto>> queryChatConversationHistory(Long userId) {
        return sessionSideBarService.queryChatConversation(userId);
    }

    @PostMapping("/conversation-create")
    public Result<ChatSessionVO> createChatConversation(@RequestBody CreateConversationCommand command) {
        return sessionSideBarService.createChatConversation(command);
    }

    @PostMapping("/conversation-delete")
    public Result<Void> deleteChatConversation(@RequestBody DeleteConversationCommand command) {
        return sessionSideBarService.deleteChatConversation(command);
    }

    @PostMapping("/conversation-title-update")
    public Result<Void> updateChatConversationTitle(@RequestBody UpdateConversationTitleCommand command) {
        return sessionSideBarService.updateChatConversationTitle(command);
    }

    @GetMapping("/messages")
    public Result<List<ChatMessageDto>> queryHistoryMessages(String memoryId) {
        return chatService.queryHistoryMessages(memoryId);
    }
}