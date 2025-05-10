package com.iohw.knobot.chat.controller;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
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


    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile4Chat(@RequestParam("file") MultipartFile file) {
        return Result.success(chatService.uploadFile4Chat(file));
    }

    @MdcDot
    @GetMapping(value = "/{memoryId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@PathVariable String memoryId, ChatRequest request) {
        return chatService.chat(memoryId, request);
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
        return Result.success(chatService.queryHistoryMessages(memoryId));
    }
}