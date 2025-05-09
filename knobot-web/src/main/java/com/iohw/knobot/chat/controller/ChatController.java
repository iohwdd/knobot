package com.iohw.knobot.chat.controller;

import com.iohw.knobot.chat.ai.assistant.AssistantService;
import com.iohw.knobot.chat.ai.assistant.IAssistant.StreamingAssistant;
import com.iohw.knobot.chat.ai.assistant.IAssistant.SummarizeAssistant;
import com.iohw.knobot.chat.ai.assistant.IAssistant.WebSearchAssistant;
import com.iohw.knobot.chat.domain.vo.response.ChatSessionResponse;
import com.iohw.knobot.chat.domain.vo.response.ChatMessageResponse;
import com.iohw.knobot.chat.domain.vo.request.ChatRequest;
import com.iohw.knobot.chat.domain.vo.request.CreateConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.DeleteConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.domain.vo.response.FileUploadResponse;
import com.iohw.knobot.chat.service.ChatService;
import com.iohw.knobot.chat.service.ConversationSideBarService;
import com.iohw.knobot.chat.domain.vo.response.ChatConversionItemResponse;
import com.iohw.knobot.common.annotation.MdcDot;
import com.iohw.knobot.upload.dto.FileUploadDTO;
import com.iohw.knobot.common.Result;
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
    private final ConversationSideBarService conversationSideBarService;
    private final ChatService chatService;
    private final WebSearchAssistant webSearchAssistant;
    private final EmbeddingStoreIngestor ingestor;
    private final AssistantService assistantService;
    private final SummarizeAssistant summarizeAssistant;

    private static final Map<String, String> filePathMap = new HashMap<>();

    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        UploadFileStrategy uploadStrategy = new LocalUploadFileStrategy();
        FileUploadDTO uploadDto = uploadStrategy.upload(file, "/tmp");

        FileUploadResponse fileUploadResponse = FileUploadResponse.builder()
                .fileId(uploadDto.getFileId())
                .fileName(uploadDto.getFileName())
                .filePath(uploadDto.getFilePath())
                .build();
        filePathMap.put(uploadDto.getFileId(), uploadDto.getFilePath());
        return Result.success(fileUploadResponse);
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

    @GetMapping("/conversation-history")
    public Result<List<ChatSessionResponse>> queryChatConversationHistory(Long userId) {
        return conversationSideBarService.queryChatConversation(userId);
    }

    @PostMapping("/conversation-create")
    public Result<ChatConversionItemResponse> createChatConversation(@RequestBody CreateConversationCommand command) {
        return conversationSideBarService.createChatConversation(command);
    }

    @PostMapping("/conversation-delete")
    public Result<Void> deleteChatConversation(@RequestBody DeleteConversationCommand command) {
        return conversationSideBarService.deleteChatConversation(command);
    }

    @PostMapping("/conversation-title-update")
    public Result<Void> updateChatConversationTitle(@RequestBody UpdateConversationTitleCommand command) {
        return conversationSideBarService.updateChatConversationTitle(command);
    }

    @GetMapping("/messages")
    public Result<List<ChatMessageResponse>> queryHistoryMessages(String memoryId) {
        return chatService.queryHistoryMessages(memoryId);
    }
}