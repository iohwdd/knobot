package com.iohw.knobot.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.iohw.knobot.common.request.CozeWorkFlowRequest;
import com.iohw.knobot.common.response.CozeWorkFlowResponse;
import okhttp3.*;

import java.io.IOException;

/**
 * @author: iohw
 * @date: 2025/4/28 23:19
 * @description:
 */
public class CozeClient {
    public static CozeWorkFlowResponse reqWorkFlow(CozeWorkFlowRequest<?> req){
        OkHttpClient client = new OkHttpClient();

        String reqJson = new Gson().toJson(req);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(reqJson, JSON);

        Request request = new Request.Builder()
                .url("https://api.coze.cn/v1/workflow/run")
                .post(body)
                // todo 配置化
                .addHeader("Authorization", "Bearer pat_h1zJDVSnghxBxOcS3KlYgX8kdzOqGCjZCt5CMMSLBJWX3XXCeawkeeQgSKgsheZk")
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
