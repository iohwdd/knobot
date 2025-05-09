package com.iohw.knobot.chat.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/13 18:16
 * @description:
 */
@Data
@Builder
public class ChatMessageResponse {
    private String role;
    private String content;
}
