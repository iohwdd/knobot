package com.iohw.knobot.chat.service.impl;

import com.iohw.knobot.chat.domain.convert.ChatConversationConverter;
import com.iohw.knobot.chat.domain.convert.ChatMessageConverter;
import com.iohw.knobot.chat.domain.entity.ChatMessageDO;
import com.iohw.knobot.chat.domain.vo.response.ChatMessageResponse;
import com.iohw.knobot.chat.mapper.ChatMessageMapper;
import com.iohw.knobot.chat.service.ChatService;
import com.iohw.knobot.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * @author: iohw
 * @date: 2025/4/13 18:16
 * @description:
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageConverter chatMessageConverter;

    @Override
    public Result<List<ChatMessageResponse>> queryHistoryMessages(String memoryId) {
        List<ChatMessageDO> chatMessageDOS = chatMessageMapper.selectByMemoryId(memoryId);
        return Result.success(chatMessageConverter.toDtoList(chatMessageDOS));
    }

    @Override
    public boolean isFirstQuestion(String memoryId) {
        // 查询该会话下用户发送的消息数量
        List<ChatMessageDO> userMessages = chatMessageMapper.selectByMemoryIdAndRole(memoryId, "user");
        return userMessages.isEmpty();
    }
}
