package com.iohw.knobot.coze.domain.vo.response;

import lombok.Data;


/**
 * @author: iohw
 * @date: 2025/4/28 23:26
 * @description:
 */
@Data
public class CozeWorkFlowResponse {
    private int code;
    private String cost;
    private String data;
    private String debug_url;
    private String msg;
    private int token;
}
