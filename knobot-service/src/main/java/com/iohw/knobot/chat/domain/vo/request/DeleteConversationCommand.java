package com.iohw.knobot.chat.domain.vo.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/22 23:16
 * @description:
 */
@Data
public class DeleteConversationCommand {
    private String memoryId;
}
