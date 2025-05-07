package com.iohw.knobot.chat.service.impl;

import com.iohw.knobot.chat.model.convert.ChatMessageConverter;
import com.iohw.knobot.chat.model.ChatMessageDO;
import com.iohw.knobot.chat.model.dto.ChatMessageDto;
import com.iohw.knobot.chat.mapper.ChatMessageMapper;
import com.iohw.knobot.chat.service.ChatService;
import com.iohw.knobot.common.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/13 18:16
 * @description:
 */
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public Result<List<ChatMessageDto>> queryHistoryMessages(String memoryId) {
        List<ChatMessageDO> chatMessageDOS = chatMessageMapper.selectByMemoryId(memoryId);
        return Result.success(ChatMessageConverter.INSTANCE.toDtoList(chatMessageDOS));
    }

    @Override
    public boolean isFirstQuestion(String memoryId) {
        // 查询该会话下用户发送的消息数量
        List<ChatMessageDO> userMessages = chatMessageMapper.selectByMemoryIdAndRole(memoryId, "user");
        return userMessages.isEmpty();
    }
}
