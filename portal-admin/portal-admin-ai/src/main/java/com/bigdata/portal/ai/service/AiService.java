package com.bigdata.portal.ai.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bigdata.portal.common.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI服务
 * 调用NVIDIA API实现文章摘要生成、AI聊天、图片生成等功能
 */
@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Value("${nvidia.api.key:}")
    private String apiKey;

    @Value("${nvidia.api.url:https://integrate.api.nvidia.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${nvidia.api.model:qwen/qwen3.5-122b-a10b}")
    private String modelName;

    private final JdbcTemplate jdbcTemplate;

    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();

    public AiService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 生成文章摘要
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
        return callNvidiaApi(prompt, "你是智讯科技资讯聚合平台的AI助手，擅长分析科技资讯并生成摘要和标签。");
    }

    /**
     * 生成文章标签
     */
    public String generateTags(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(400, "文章内容不能为空");
        }
        String prompt = String.format(
                "请为以下科技资讯文章生成3-5个中文标签，用逗号分隔，只输出标签不要其他内容：\n\n%s",
                content
        );
        return callNvidiaApi(prompt, "你是智讯科技资讯聚合平台的AI助手，擅长分析科技资讯并生成摘要和标签。");
    }

    /**
     * AI聊天
     */
    public String chat(String message, String context) {
        if (message == null || message.trim().isEmpty()) {
            throw new CustomException(400, "消息不能为空");
        }
        String systemPrompt = "你是智讯科技资讯聚合平台的AI助手。你擅长：\n" +
                "1. 科技资讯分析和解读\n" +
                "2. 技术趋势预测和讨论\n" +
                "3. 数据分析和可视化建议\n" +
                "4. 通用知识问答\n" +
                "请用中文回答，回答要准确、专业、有深度。";
        if (context != null && !context.trim().isEmpty()) {
            systemPrompt += "\n\n当前上下文信息：\n" + context;
        }
        return callNvidiaApi(message, systemPrompt);
    }

    /**
     * AI图片生成
     * 使用NVIDIA的文生图模型
     */
    public String generateImage(String prompt, Integer width, Integer height) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new CustomException(400, "图片描述不能为空");
        }
        try {
            // 使用NVIDIA的文生图API
            String imageApiUrl = "https://ai.api.nvidia.com/v1/genai/stabilityai/stable-diffusion-xl";
            URL url = new URL(imageApiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(120000);

            JSONObject requestBody = new JSONObject();
            requestBody.put("prompt", prompt);
            requestBody.put("width", width != null ? width : 1024);
            requestBody.put("height", height != null ? height : 1024);
            requestBody.put("seed", 0);
            requestBody.put("steps", 50);
            requestBody.put("cfg_scale", 7);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

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
                log.error("NVIDIA Image API调用失败，状态码：{}，响应：{}", responseCode, response);
                // 回退：使用文本模型描述图片
                return fallbackImageDescription(prompt);
            }

            JSONObject jsonResponse = JSON.parseObject(response.toString());
            // 尝试从响应中提取图片URL
            if (jsonResponse.containsKey("data")) {
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                if (dataArray != null && !dataArray.isEmpty()) {
                    JSONObject firstItem = dataArray.getJSONObject(0);
                    if (firstItem.containsKey("url")) {
                        return firstItem.getString("url");
                    }
                    if (firstItem.containsKey("b64_json")) {
                        return "data:image/png;base64," + firstItem.getString("b64_json");
                    }
                }
            }

            // 如果响应格式不匹配，回退到文本描述
            return fallbackImageDescription(prompt);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用NVIDIA Image API异常：", e);
            return fallbackImageDescription(prompt);
        }
    }

    /**
     * 回退方案：使用文本模型生成图片描述
     */
    private String fallbackImageDescription(String prompt) {
        String imagePrompt = String.format(
                "用户想要生成一张图片，描述是：「%s」。请用详细的文字描述这张图片应该是什么样子的，包括构图、色彩、氛围等，200字以内。",
                prompt
        );
        return callNvidiaApi(imagePrompt, "你是一个专业的视觉艺术顾问，擅长用文字描述画面。");
    }

    /**
     * AI智能搜索新闻
     * 基于关键词搜索新闻并让AI分析
     */
    public String searchNews(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException(400, "搜索关键词不能为空");
        }
        String newsData = fetchNewsByKeyword(keyword);
        if (newsData.isEmpty()) {
            return "未找到与「" + keyword + "」相关的新闻，请尝试其他关键词。";
        }
        String prompt = String.format(
                "用户搜索关键词：「%s」\n\n以下是搜索到的相关新闻：\n%s\n\n" +
                "请对以上新闻进行分析总结，包括：\n" +
                "1. 核心事件和关键信息\n" +
                "2. 行业趋势和影响\n" +
                "3. 相关技术解读\n" +
                "请用中文回答，条理清晰，重点突出。",
                keyword, newsData
        );
        return callNvidiaApi(prompt, "你是智讯科技资讯聚合平台的AI搜索助手，擅长分析科技资讯并提炼关键信息。");
    }

    /**
     * AI智能搜索新闻（SSE流式）
     */
    public SseEmitter searchNewsStream(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException(400, "搜索关键词不能为空");
        }
        SseEmitter emitter = new SseEmitter(120000L);
        sseExecutor.execute(() -> {
            try {
                String newsData = fetchNewsByKeyword(keyword);
                String prompt;
                if (newsData.isEmpty()) {
                    prompt = "用户搜索关键词：「" + keyword + "」，但未找到相关新闻。请告知用户未找到结果，并建议尝试其他关键词。";
                } else {
                    prompt = String.format(
                            "用户搜索关键词：「%s」\n\n以下是搜索到的相关新闻：\n%s\n\n" +
                            "请对以上新闻进行分析总结，包括：\n" +
                            "1. 核心事件和关键信息\n" +
                            "2. 行业趋势和影响\n" +
                            "3. 相关技术解读\n" +
                            "请用中文回答，条理清晰，重点突出。",
                            keyword, newsData
                    );
                }
                callNvidiaApiStream(prompt, "你是智讯科技资讯聚合平台的AI搜索助手，擅长分析科技资讯并提炼关键信息。", emitter);
                emitter.complete();
            } catch (Exception e) {
                log.error("SSE搜索流式异常：", e);
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    /**
     * AI热点总结
     * 获取最新新闻并生成总结
     */
    public String hotSummary(String instruction) {
        String newsData = fetchLatestNews();
        if (newsData.isEmpty()) {
            return "暂无最新新闻数据。";
        }
        String prompt = String.format(
                "以下是平台最新的科技新闻：\n%s\n\n" +
                "请对这些新闻进行热点总结%s" +
                "要求：\n" +
                "1. 提炼最热门的3-5个话题\n" +
                "2. 每个话题简要说明核心内容和影响\n" +
                "3. 总结整体科技趋势\n" +
                "请用中文回答，条理清晰。",
                newsData,
                instruction != null && !instruction.trim().isEmpty()
                        ? "，重点关注：" + instruction + "\n" : "。\n"
        );
        return callNvidiaApi(prompt, "你是智讯科技资讯聚合平台的AI热点分析助手，擅长从大量资讯中提炼热点和趋势。");
    }

    /**
     * AI热点总结（SSE流式）
     */
    public SseEmitter hotSummaryStream(String instruction) {
        SseEmitter emitter = new SseEmitter(120000L);
        sseExecutor.execute(() -> {
            try {
                String newsData = fetchLatestNews();
                String prompt;
                if (newsData.isEmpty()) {
                    prompt = "平台暂无最新新闻数据，请告知用户当前没有可分析的热点新闻。";
                } else {
                    prompt = String.format(
                            "以下是平台最新的科技新闻：\n%s\n\n" +
                            "请对这些新闻进行热点总结%s" +
                            "要求：\n" +
                            "1. 提炼最热门的3-5个话题\n" +
                            "2. 每个话题简要说明核心内容和影响\n" +
                            "3. 总结整体科技趋势\n" +
                            "请用中文回答，条理清晰。",
                            newsData,
                            instruction != null && !instruction.trim().isEmpty()
                                    ? "，重点关注：" + instruction + "\n" : "。\n"
                    );
                }
                callNvidiaApiStream(prompt, "你是智讯科技资讯聚合平台的AI热点分析助手，擅长从大量资讯中提炼热点和趋势。", emitter);
                emitter.complete();
            } catch (Exception e) {
                log.error("SSE热点总结流式异常：", e);
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    /**
     * AI智能翻译
     */
    public String translate(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new CustomException(400, "翻译文本不能为空");
        }
        String prompt = String.format("请将以下内容翻译成中文（如果原文是中文则翻译成英文）：\n\n%s", text);
        return callNvidiaApi(prompt, "你是一个专业的科技领域翻译助手，擅长中英文互译，翻译要准确、专业、流畅。");
    }

    /**
     * 根据关键词搜索新闻数据
     */
    private String fetchNewsByKeyword(String keyword) {
        try {
            String sql = "SELECT title, summary, channel, source, publish_time FROM news " +
                    "WHERE title LIKE ? OR summary LIKE ? OR channel LIKE ? " +
                    "ORDER BY publish_time DESC LIMIT 10";
            String likeKeyword = "%" + keyword + "%";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, likeKeyword, likeKeyword, likeKeyword);
            return formatNewsData(rows);
        } catch (Exception e) {
            log.error("搜索新闻数据异常：", e);
            return "";
        }
    }

    /**
     * 获取最新新闻数据
     */
    private String fetchLatestNews() {
        try {
            String sql = "SELECT title, summary, channel, source, publish_time FROM news " +
                    "ORDER BY publish_time DESC LIMIT 15";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            return formatNewsData(rows);
        } catch (Exception e) {
            log.error("获取最新新闻数据异常：", e);
            return "";
        }
    }

    /**
     * 格式化新闻数据为文本
     */
    private String formatNewsData(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Map<String, Object> row : rows) {
            sb.append(index++).append(". ");
            sb.append("【").append(row.get("channel")).append("】");
            sb.append(row.get("title"));
            if (row.get("summary") != null && !row.get("summary").toString().isEmpty()) {
                sb.append(" - ").append(row.get("summary"));
            }
            sb.append("（来源：").append(row.get("source")).append("）");
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 调用NVIDIA Chat API（流式，SSE）
     */
    private void callNvidiaApiStream(String prompt, String systemPrompt, SseEmitter emitter) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(120000);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            requestBody.put("temperature", 0.6);
            requestBody.put("max_tokens", 16384);
            requestBody.put("top_p", 0.95);
            requestBody.put("stream", true);

            JSONArray messages = new JSONArray();

            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                }
                log.error("NVIDIA Stream API调用失败，状态码：{}，响应：{}", responseCode, errorResponse);
                emitter.send(SseEmitter.event().data("[ERROR] AI服务调用失败"));
                return;
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            break;
                        }
                        try {
                            JSONObject chunk = JSON.parseObject(data);
                            JSONArray choices = chunk.getJSONArray("choices");
                            if (choices != null && !choices.isEmpty()) {
                                JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                                if (delta != null && delta.containsKey("content")) {
                                    String content = delta.getString("content");
                                    if (content != null && !content.isEmpty()) {
                                        emitter.send(SseEmitter.event().data(content));
                                    }
                                }
                            }
                        } catch (Exception parseEx) {
                            // 忽略非JSON行
                        }
                    }
                }
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用NVIDIA Stream API异常：", e);
            try {
                emitter.send(SseEmitter.event().data("[ERROR] AI服务调用异常：" + e.getMessage()));
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 调用NVIDIA Chat API
     */
    private String callNvidiaApi(String prompt, String systemPrompt) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            requestBody.put("temperature", 0.6);
            requestBody.put("max_tokens", 16384);
            requestBody.put("top_p", 0.95);

            JSONArray messages = new JSONArray();

            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

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
