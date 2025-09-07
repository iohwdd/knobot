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
    private String content;           // 消息内容
    private String enhancedContent;
    private Integer userTokens;       // 用户层面token
    private Integer systemTokens;     // 系统实际token消耗
    private LocalDateTime createTime;
}