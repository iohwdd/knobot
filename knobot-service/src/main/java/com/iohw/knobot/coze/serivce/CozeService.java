package com.iohw.knobot.coze.serivce;

import com.iohw.knobot.chat.domain.dto.WeatherDataDTO;
import com.iohw.knobot.chat.domain.vo.request.DayWhetherRequest;

/**
 * @author: iohw
 * @date: 2025/4/29 23:22
 * @description:
 */
public interface CozeService {
    WeatherDataDTO getWeatherData(DayWhetherRequest request);
}
