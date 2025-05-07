package com.iohw.knobot.chat.service.impl;

import com.iohw.knobot.chat.model.ChatMessageDO;
import com.iohw.knobot.chat.model.convert.ChatConversationConverter;
import com.iohw.knobot.chat.model.enums.ChatSessionEnum;
import com.iohw.knobot.chat.mapper.ChatMessageMapper;
import com.iohw.knobot.chat.mapper.ChatConversationMapper;
import com.iohw.knobot.chat.request.command.CreateConversationCommand;
import com.iohw.knobot.chat.request.command.DeleteConversationCommand;
import com.iohw.knobot.chat.request.command.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.service.SessionSideBarService;
import com.iohw.knobot.chat.vo.ChatSessionVO;
import com.iohw.knobot.common.response.Result;
import com.iohw.knobot.utils.IdGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iohw.knobot.chat.model.dto.ChatSessionDto;
import com.iohw.knobot.chat.model.ChatConversationDO;
import java.util.List;
import java.util.UUID;

import static com.iohw.knobot.chat.constant.ChatConstant.NEW_SESSION_TITLE;

/**
 * @author: iohw
 * @date: 2025/4/13 17:27
 * @description:
 */
@Service
public class SessionSideBarServiceImpl implements SessionSideBarService {
    @Autowired
    private ChatConversationMapper chatConversationMapper;
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public Result<List<ChatSessionDto>> queryChatConversation(long userId) {
        List<ChatConversationDO> chatConversationDOS = chatConversationMapper.selectByUserId(userId, 0);
        return Result.success(ChatConversationConverter.INSTANCE.toDtoList(chatConversationDOS));
    }

    @Override
    public Result<ChatSessionVO> createChatConversation(CreateConversationCommand request) {
        String memoryId = UUID.randomUUID().toString();
        Long userId = request.getUserId();
        ChatConversationDO chatConversationDO = ChatConversationDO.builder()
                .title(NEW_SESSION_TITLE)
                .status(ChatSessionEnum.NORMAL.getStatus())
                .memoryId(memoryId)
                .userId(userId)
                .build();
        chatConversationMapper.insert(chatConversationDO);
        // 附上机器人问候语
        ChatMessageDO chatMessageDO = ChatMessageDO.builder()
                .messageId(IdGeneratorUtil.generateId())
                .role("assistant")
                .memoryId(memoryId)
                .content("你好呀~很高兴能跟你交流😄")
                .build();
        chatMessageMapper.insert(chatMessageDO);

        return Result.success(ChatSessionVO.builder()
                .title(NEW_SESSION_TITLE)
                .memoryId(memoryId)
                .build());
    }

    @Override
    public Result<Void> deleteChatConversation(DeleteConversationCommand request) {
        chatConversationMapper.updateStatus(request.getMemoryId(), 2);
        return Result.success(null);
    }

    @Override
    public Result<Void> updateChatConversationTitle(UpdateConversationTitleCommand command) {
        chatConversationMapper.updateTitle(command.getMemoryId(), command.getNewTitle());
        return Result.success(null);
    }
}
