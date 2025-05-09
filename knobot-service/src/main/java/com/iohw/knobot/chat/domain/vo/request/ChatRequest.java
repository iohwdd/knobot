package com.iohw.knobot.chat.domain.vo.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/17 21:25
 * @description:
 */
@Data
public class ChatRequest {
    private String knowledgeLibId;
    private String userId;
    private String userMessage;
    private String fileId;
    private Boolean isWebSearchRequest;
}
