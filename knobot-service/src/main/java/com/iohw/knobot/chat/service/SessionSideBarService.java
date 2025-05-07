package com.iohw.knobot.chat.service;


import com.iohw.knobot.chat.model.dto.ChatSessionDto;
import com.iohw.knobot.chat.request.command.CreateConversationCommand;
import com.iohw.knobot.chat.request.command.DeleteConversationCommand;
import com.iohw.knobot.chat.request.command.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.vo.ChatSessionVO;
import com.iohw.knobot.common.response.Result;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/13 17:24
 * @description:
 */
public interface SessionSideBarService {
    Result<List<ChatSessionDto>> queryChatConversation(long userId);

    Result<ChatSessionVO> createChatConversation(CreateConversationCommand userId);

    Result<Void> deleteChatConversation(DeleteConversationCommand request);

    Result<Void> updateChatConversationTitle(UpdateConversationTitleCommand command);

}
