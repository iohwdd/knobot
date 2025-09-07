package com.iohw.knobot.coze.serivce.impl;

import com.alibaba.fastjson.JSONObject;
import com.iohw.knobot.coze.domain.vo.request.CozeWorkFlowRequest;
import com.iohw.knobot.coze.domain.vo.response.CozeWorkFlowResponse;
import com.iohw.knobot.chat.domain.dto.WeatherDataDTO;
import com.iohw.knobot.chat.domain.vo.request.DayWhetherRequest;
import com.iohw.knobot.coze.serivce.ICozeService;
import com.iohw.knobot.coze.CozeClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author: iohw
 * @date: 2025/4/29 23:22
 * @description:
 */
@Service
@RequiredArgsConstructor
public class ICozeServiceImpl implements ICozeService {
    final CozeClient cozeClient;

    @Override
    public WeatherDataDTO getWeatherData(DayWhetherRequest request) {
        CozeWorkFlowRequest<DayWhetherRequest> workFlowRequest = new CozeWorkFlowRequest<>();
        workFlowRequest.setWorkflow_id("7547187910912065575");
        workFlowRequest.setParameters(request);

        CozeWorkFlowResponse workFlowResponse = cozeClient.reqWorkFlow(workFlowRequest);
        JSONObject jsonObject = JSONObject.parseObject(workFlowResponse.getData());
        WeatherDataDTO data = JSONObject.parseObject(jsonObject.getString("data"), WeatherDataDTO.class);

        return data;
    }
}
