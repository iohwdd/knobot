package com.iohw.knobot.chat.service;


import com.iohw.knobot.chat.domain.vo.response.ChatSessionResponse;
import com.iohw.knobot.chat.domain.vo.request.CreateConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.DeleteConversationCommand;
import com.iohw.knobot.chat.domain.vo.request.UpdateConversationTitleCommand;
import com.iohw.knobot.chat.domain.vo.response.ChatConversionItemResponse;
import com.iohw.knobot.common.Result;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/13 17:24
 * @description:
 */
public interface IConversationSideBarService {
    Result<List<ChatSessionResponse>> queryChatConversation(long userId);

    Result<ChatConversionItemResponse> createChatConversation(CreateConversationCommand userId);

    Result<Void> deleteChatConversation(DeleteConversationCommand request);

    Result<Void> updateChatConversationTitle(UpdateConversationTitleCommand command);

}
