package com.iohw.knobot.home.controller;

import com.iohw.knobot.chat.assistant.IAssistant.EmailAssistant;
import com.iohw.knobot.chat.request.command.SubmitIssueCommand;
import com.iohw.knobot.chat.tool.SendEmailTool;
import com.iohw.knobot.common.response.Result;
import com.iohw.knobot.coze.model.WeatherData;
import com.iohw.knobot.coze.request.DayWhetherRequest;
import com.iohw.knobot.home.serivce.CozeService;
import com.iohw.knobot.utils.EmailUtil;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: iohw
 * @date: 2025/4/23 23:04
 * @description: 首页控制器
 */
@Slf4j
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final CozeService cozeService;
    private final EmailUtil emailUtil;

    @PostMapping("/submitIssue")
    public Result<Void> submitIssue(@RequestBody SubmitIssueCommand issue) {
        emailUtil.sendFeedbackEmail(issue.getTitle(), issue.getIssueDescription());
        return Result.success(null);
    }

    @PostMapping("/getWeather")
    public Result<WeatherData> getWeather(@RequestBody DayWhetherRequest request) {
        return Result.success(cozeService.getWeatherData(request));
    }
}