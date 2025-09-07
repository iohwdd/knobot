package com.iohw.knobot.coze;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.iohw.knobot.coze.domain.vo.request.CozeWorkFlowRequest;
import com.iohw.knobot.coze.domain.vo.response.CozeWorkFlowResponse;

import okhttp3.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: iohw
 * @date: 2025/4/28 23:19
 * @description:
 */
@Component
public class CozeClient {
    @Value("${coze.token}")
    private String token;

    public CozeWorkFlowResponse reqWorkFlow(CozeWorkFlowRequest<?> req){
        OkHttpClient client = new OkHttpClient();

        String reqJson = new Gson().toJson(req);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(reqJson, JSON);

        Request request = new Request.Builder()
                .url("https://api.coze.cn/v1/workflow/run")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String respJson = response.body().string();
            return JSONObject.parseObject(respJson, CozeWorkFlowResponse.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
