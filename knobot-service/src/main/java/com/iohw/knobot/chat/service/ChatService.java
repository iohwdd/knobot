package com.iohw.knobot.chat.service;

import com.iohw.knobot.chat.domain.vo.request.ChatRequest;
import com.iohw.knobot.chat.domain.vo.response.ChatMessageResponse;
import com.iohw.knobot.chat.domain.vo.response.FileUploadResponse;
import com.iohw.knobot.common.Result;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author: iohw
 * @date: 2025/4/13 18:16
 * @description:
 */
public interface ChatService {
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
     * 判断是否是用户在该会话中的第一次提问
     * @param memoryId 会话ID
     * @return true 如果是第一次提问，false 否则
     */
    boolean isFirstQuestion(String memoryId);

    /**
     * 在对话中上传文件
     *
     * @param file
     * @return
     */
    FileUploadResponse uploadFile4Chat(MultipartFile file);


}
