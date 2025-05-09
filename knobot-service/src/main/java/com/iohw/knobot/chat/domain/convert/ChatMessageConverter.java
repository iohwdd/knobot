package com.iohw.knobot.chat.domain.convert;

import com.iohw.knobot.chat.domain.entity.ChatMessageDO;
import com.iohw.knobot.chat.domain.vo.response.ChatMessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/13 18:17
 * @description:
 */
@Mapper(componentModel = "spring")
public interface ChatMessageConverter {
    @Mapping(target = "role", source = "role")
    @Mapping(target = "content", source = "content")
    ChatMessageResponse toDto(ChatMessageDO chatMessageDO);

    List<ChatMessageResponse> toDtoList(List<ChatMessageDO> chatMessageDOS);
}
