package com.iohw.knobot.chat.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/13 17:26
 * @description:
 */
@Data
@Builder
public class ChatSessionResponse {
    private String memoryId;
    private String userId;
    private String title;
}
