package com.iohw.knobot.chat.service.impl;

import com.iohw.knobot.chat.domain.entity.ChatMessageDO;
import com.iohw.knobot.chat.domain.convert.ChatConversationConverter;
import com.iohw.knobot.common.enums.ChatConversionEnum;
import com.iohw.knobot.chat.mapper.ChatMessageMapper;
import com.iohw.knobot.chat.mapper.ChatConversationMapper;
import com.iohw.knobot.chat.domain.vo.request.CreateConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.DeleteConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.service.IConversationSideBarService;
import com.iohw.knobot.chat.domain.vo.response.ChatConversionItemResponse;
import com.iohw.knobot.common.Result;
import com.iohw.knobot.utils.IdGeneratorUtil;

import org.springframework.stereotype.Service;
import com.iohw.knobot.chat.domain.vo.response.ChatSessionResponse;
import com.iohw.knobot.chat.domain.entity.ChatConversationDO;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import static com.iohw.knobot.chat.domain.common.constant.ChatConstant.NEW_SESSION_TITLE;

/**
 * @author: iohw
 * @date: 2025/4/13 17:27
 * @description:
 */
@Service
@RequiredArgsConstructor
public class ConversationSideBarServiceImpl implements IConversationSideBarService {
    private final ChatConversationMapper chatConversationMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatConversationConverter chatConversationConverter;

    @Override
    public Result<List<ChatSessionResponse>> queryChatConversation(long userId) {
        List<ChatConversationDO> chatConversationDOS = chatConversationMapper.selectByUserId(userId, 0);
        return Result.success(chatConversationConverter.toDtoList(chatConversationDOS));
    }

    @Override
    public Result<ChatConversionItemResponse> createChatConversation(CreateConversationCommand request) {
        String memoryId = UUID.randomUUID().toString();
        Long userId = request.getUserId();
        ChatConversationDO chatConversationDO = ChatConversationDO.builder()
                .title(NEW_SESSION_TITLE)
                .status(ChatConversionEnum.NORMAL.getStatus())
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

        return Result.success(ChatConversionItemResponse.builder()
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
