package com.iohw.knobot.chat.domain.convert;

import org.mapstruct.Mapper;

import java.util.List;

import com.iohw.knobot.chat.domain.entity.ChatConversationDO;
import com.iohw.knobot.chat.domain.vo.response.ChatSessionResponse;

/**
 * @author: iohw
 * @date: 2025/4/13 17:34
 * @description:
 */
@Mapper(componentModel = "spring")
public interface ChatConversationConverter {
    ChatSessionResponse toDto(ChatConversationDO chatConversationDO);

    List<ChatSessionResponse> toDtoList(List<ChatConversationDO> chatConversationDO);
}
