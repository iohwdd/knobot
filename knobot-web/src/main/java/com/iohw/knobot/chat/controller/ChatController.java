package com.iohw.knobot.chat.controller;

import com.iohw.knobot.chat.domain.vo.response.ChatSessionResponse;
import com.iohw.knobot.chat.domain.vo.response.ChatMessageResponse;
import com.iohw.knobot.chat.domain.vo.request.ChatRequest;
import com.iohw.knobot.chat.domain.vo.request.CreateConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.DeleteConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.domain.vo.response.FileUploadResponse;
import com.iohw.knobot.chat.service.IChatService;
import com.iohw.knobot.chat.service.IConversationSideBarService;
import com.iohw.knobot.chat.domain.vo.response.ChatConversionItemResponse;
import com.iohw.knobot.common.annotation.MdcDot;
import com.iohw.knobot.common.Result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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
    private final IConversationSideBarService IConversationSideBarService;
    private final IChatService IChatService;


    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile4Chat(@RequestParam("file") MultipartFile file) {
        return Result.success(IChatService.uploadFile4Chat(file));
    }

    @MdcDot
    @GetMapping(value = "/{memoryId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@PathVariable String memoryId, ChatRequest request) {
        return IChatService.chat(memoryId, request);
    }


    @GetMapping("/conversation-history")
    public Result<List<ChatSessionResponse>> queryChatConversationHistory(Long userId) {
        return IConversationSideBarService.queryChatConversation(userId);
    }

    @PostMapping("/conversation-create")
    public Result<ChatConversionItemResponse> createChatConversation(@RequestBody CreateConversationCommand command) {
        return IConversationSideBarService.createChatConversation(command);
    }

    @PostMapping("/conversation-delete")
    public Result<Void> deleteChatConversation(@RequestBody DeleteConversationCommand command) {
        return IConversationSideBarService.deleteChatConversation(command);
    }

    @PostMapping("/conversation-title-update")
    public Result<Void> updateChatConversationTitle(@RequestBody UpdateConversationTitleCommand command) {
        return IConversationSideBarService.updateChatConversationTitle(command);
    }

    @GetMapping("/messages")
    public Result<List<ChatMessageResponse>> queryHistoryMessages(String memoryId) {
        return Result.success(IChatService.queryHistoryMessages(memoryId));
    }
}