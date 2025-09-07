package com.iohw.knobot.chat.service;

import com.iohw.knobot.chat.domain.vo.request.ChatRequest;
import com.iohw.knobot.chat.domain.vo.response.ChatMessageResponse;
import com.iohw.knobot.chat.domain.vo.response.FileUploadResponse;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author: iohw
 * @date: 2025/4/13 18:16
 * @description:
 */
public interface IChatService {
    /**
     * 流式对话
     * @param memoryId
     * @param request
     * @return
     */
    SseEmitter chat(String memoryId, ChatRequest request);

    /**
     *
     * @param memoryId
     * @return
     */
    List<ChatMessageResponse> queryHistoryMessages(String memoryId);


    /**
     * 在对话中上传文件
     *
     * @param file
     * @return
     */
    FileUploadResponse uploadFile4Chat(MultipartFile file);


}
