package com.iohw.knobot.chat.domain.dto;

import lombok.Data;

@Data
public class WeatherDataDTO {
    private String condition;
    private Double temp_high;
    private String weather_day;
    private String wind_dir_day;
    private String wind_dir_night;
    private Double humidity;
    private String predict_date;
    private Double temp_low;
    private String wind_level_day;
    private String wind_level_night;
}