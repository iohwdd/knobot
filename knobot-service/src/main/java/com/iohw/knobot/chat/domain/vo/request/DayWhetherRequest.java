package com.iohw.knobot.chat.domain.vo.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/29 22:52
 * @description:
 */
@Data
public class DayWhetherRequest {
    private String city;
    private String endTime;
    private String province;
    private String startTime;
    private String towns;
    private String villages;
}
