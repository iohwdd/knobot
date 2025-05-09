package com.iohw.knobot.coze.domain.vo.request;

import lombok.Data;

import java.util.Map;

/**
 * @author: iohw
 * @date: 2025/4/28 23:20
 * @description:
 */
@Data
public class CozeWorkFlowRequest<T> {
    private String workflow_id;
    private T parameters;
    private String bot_id;
    private Map<String,String> ext;
    private boolean is_async;
    private String app_id;
}
