package com.iohw.knobot.chat.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatConversationDO {
    private Long id;
    private String memoryId;
    private Long userId;
    private String title;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}