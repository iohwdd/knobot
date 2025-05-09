package com.iohw.knobot.chat.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageDO {
    private Long id;
    private String messageId;
    private String memoryId;
    private String role;
    private String content;
    private String enhancedContent;
    private Integer tokens;
    private LocalDateTime createTime;
}