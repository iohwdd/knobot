package com.iohw.knobot.coze.domain.vo.response;

import com.iohw.knobot.chat.domain.dto.WeatherDataDTO;
import lombok.Data;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/29 22:55
 * @description:
 */
@Data
public class DayWhetherResponse {
    private Integer code;
    private List<WeatherDataDTO> data;
    private String message;

}
