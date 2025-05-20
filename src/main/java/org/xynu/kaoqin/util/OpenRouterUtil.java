package org.xynu.kaoqin.util;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenRouterUtil {
    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String API_KEY = "sk-xxqiqdokcaaqjvxefinvqhpgvekednxrupljvfwngkyzoppz";

    public static String askModel(String prompt) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .build();
        JSONObject body = new JSONObject();
        body.put("model", "deepseek-ai/DeepSeek-V3");
        body.put("messages", new org.json.JSONArray()
            .put(new JSONObject().put("role", "user").put("content", prompt))
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String resp = response.body().string();
            System.out.println("AI原始返回内容：" + resp);
            if (resp != null && resp.trim().startsWith("{")) {
                try {
                    JSONObject json = new JSONObject(resp);
                    return json.getJSONArray("choices")
                               .getJSONObject(0)
                               .getJSONObject("message")
                               .getString("content");
                } catch (Exception e) {
                    return "AI返回内容格式异常：" + resp;
                }
            } else {
                return "AI返回内容异常：" + resp;
            }
        }
    }
}