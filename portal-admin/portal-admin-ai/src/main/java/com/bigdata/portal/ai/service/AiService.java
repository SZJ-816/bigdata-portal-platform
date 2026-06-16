package com.bigdata.portal.ai.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bigdata.portal.common.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * AI服务
 * 调用NVIDIA API实现文章摘要生成等功能
 */
@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    /** NVIDIA API密钥 */
    @Value("${nvidia.api.key:}")
    private String apiKey;

    /** NVIDIA API地址 */
    @Value("${nvidia.api.url:https://integrate.api.nvidia.com/v1/chat/completions}")
    private String apiUrl;

    /** 使用的模型名称 */
    @Value("${nvidia.api.model:nvidia/llama-3.1-nemotron-70b-instruct}")
    private String modelName;

    /**
     * 生成文章摘要
     *
     * @param content 文章内容
     * @param maxLength 摘要最大长度
     * @return 生成的摘要
     */
    public String generateSummary(String content, Integer maxLength) {
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(400, "文章内容不能为空");
        }

        int limit = maxLength != null ? maxLength : 200;

        String prompt = String.format(
                "请为以下科技资讯文章生成一段简洁的中文摘要，不超过%d个字，突出核心要点：\n\n%s",
                limit, content
        );

        return callNvidiaApi(prompt);
    }

    /**
     * 生成文章标签
     *
     * @param content 文章内容
     * @return 标签列表（逗号分隔）
     */
    public String generateTags(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(400, "文章内容不能为空");
        }

        String prompt = String.format(
                "请为以下科技资讯文章生成3-5个中文标签，用逗号分隔，只输出标签不要其他内容：\n\n%s",
                content
        );

        return callNvidiaApi(prompt);
    }

    /**
     * 调用NVIDIA API
     *
     * @param prompt 提示词
     * @return API返回的文本
     */
    private String callNvidiaApi(String prompt) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);

            JSONArray messages = new JSONArray();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是智讯科技资讯聚合平台的AI助手，擅长分析科技资讯并生成摘要和标签。");
            messages.add(systemMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            if (responseCode != 200) {
                log.error("NVIDIA API调用失败，状态码：{}，响应：{}", responseCode, response);
                throw new CustomException(500, "AI服务调用失败");
            }

            // 解析响应
            JSONObject jsonResponse = JSON.parseObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content");
            }

            throw new CustomException(500, "AI服务返回数据异常");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用NVIDIA API异常：", e);
            throw new CustomException(500, "AI服务调用异常：" + e.getMessage());
        }
    }
}
