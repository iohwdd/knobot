package com.iohw.knobot.chat.service;

import com.iohw.knobot.chat.model.dto.ChatMessageDto;
import com.iohw.knobot.common.response.Result;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/13 18:16
 * @description:
 */
public interface ChatService {
    Result<List<ChatMessageDto>> queryHistoryMessages(String memoryId);

    /**
     * 判断是否是用户在该会话中的第一次提问
     * @param memoryId 会话ID
     * @return true 如果是第一次提问，false 否则
     */
    boolean isFirstQuestion(String memoryId);
}
