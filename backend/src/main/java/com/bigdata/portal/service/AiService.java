package com.bigdata.portal.service;

import com.bigdata.portal.entity.News;
import com.bigdata.portal.mapper.NewsMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class AiService {

    @Autowired
    private NewsMapper newsMapper;

    @Value("${ai.nvidia.base-url:https://integrate.api.nvidia.com/v1}")
    private String baseUrl;

    @Value("${ai.nvidia.api-key:}")
    private String apiKey;

    @Value("${ai.nvidia.model:meta/llama-3.1-8b-instruct}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT_SEARCH = "你是「TechNews AI」科技资讯智能助手，专注于科技行业分析和解读。\n" +
            "你的职责：\n" +
            "1. 基于平台新闻数据，为用户提供专业、深度的科技资讯分析\n" +
            "2. 结合多源新闻进行综合分析，发现趋势和关联\n" +
            "3. 用清晰的结构化格式回答，善用标题、列表和加粗\n" +
            "4. 回答必须基于提供的新闻内容，不编造不存在的信息\n" +
            "5. 如果新闻数据不足以回答，诚实说明并给出你的专业见解\n" +
            "回答风格：专业但不晦涩，简洁但信息量充足，善用数据支撑观点。";

    private static final String SYSTEM_PROMPT_HOT_SUMMARY = "你是「TechNews AI」科技资讯主编，负责每日热点总结。\n" +
            "你的职责：\n" +
            "1. 从今日新闻中提炼最重要的科技热点\n" +
            "2. 按领域分类（AI、硬件、互联网、创业等）\n" +
            "3. 每个热点用1-2句话概括核心信息\n" +
            "4. 最后给出今日科技趋势一句话总结\n" +
            "格式要求：使用**加粗**标记关键词，用编号列表组织内容。";

    private volatile String cachedHotSummary = null;
    private volatile long cachedHotSummaryTime = 0;
    private static final long HOT_SUMMARY_CACHE_TTL = 3600000;

    private static final int MAX_KEYWORD_LENGTH = 100;
    private static final int MAX_INSTRUCTION_LENGTH = 500;
    private static final int MAX_SEARCH_RESULT_LENGTH = 10000;

    public interface StreamCallback {
        void onToken(String token);
        void onComplete();
        void onError(Exception e);
    }

    private String buildSearchContext(String keyword) {
        List<News> searchResults = newsMapper.search(keyword);
        List<News> channelResults = newsMapper.findByChannelTop20(keyword);
        if (channelResults != null) {
            for (News n : channelResults) {
                if (searchResults.stream().noneMatch(s -> s.getId().equals(n.getId()))) {
                    searchResults.add(n);
                }
            }
        }
        if (searchResults.isEmpty()) {
            List<News> topNews = newsMapper.findTop20();
            if (topNews != null && !topNews.isEmpty()) {
                searchResults.addAll(topNews.subList(0, Math.min(5, topNews.size())));
            }
        }
        if (searchResults.isEmpty()) {
            return "暂无相关新闻数据";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (News news : searchResults) {
            if (count >= 10) break;
            sb.append(count + 1).append(". 【");
            sb.append(news.getChannel() != null ? news.getChannel() : "综合");
            sb.append("】");
            sb.append(news.getTitle());
            if (news.getSummary() != null && !news.getSummary().isEmpty()) {
                String summary = news.getSummary();
                if (summary.length() > 150) {
                    summary = summary.substring(0, 150) + "...";
                }
                sb.append("\n   摘要：").append(summary);
            }
            if (news.getSource() != null && !news.getSource().isEmpty()) {
                sb.append("（来源：").append(news.getSource()).append("）");
            }
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }

    private String buildSearchPrompt(String keyword) {
        String context = buildSearchContext(keyword);
        return "以下是平台中与「" + keyword + "」相关的最新科技新闻：\n\n" + context +
                "\n请根据以上新闻，对「" + keyword + "」进行深度分析，包括：\n" +
                "1. 核心动态：最近发生了什么重要事件？\n" +
                "2. 行业影响：这对科技行业意味着什么？\n" +
                "3. 趋势判断：未来可能如何发展？\n\n" +
                "请用中文回答，800字以内。";
    }

    private String buildHotSummaryContext() {
        List<News> allNews = newsMapper.findTop20();
        if (allNews == null || allNews.isEmpty()) {
            return "暂无新闻";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (News news : allNews) {
            if (count >= 15) break;
            sb.append(count + 1).append(". 【");
            sb.append(news.getChannel() != null ? news.getChannel() : "综合");
            sb.append("】");
            sb.append(news.getTitle());
            if (news.getSummary() != null && !news.getSummary().isEmpty()) {
                String summary = news.getSummary();
                if (summary.length() > 100) {
                    summary = summary.substring(0, 100) + "...";
                }
                sb.append(" - ").append(summary);
            }
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }

    private String buildHotSummaryPrompt(String instruction) {
        String context = buildHotSummaryContext();
        String prompt = "以下是今日平台收录的最新科技新闻：\n\n" + context +
                "\n请根据以上新闻生成今日科技热点总结，要求：\n" +
                "1. 按领域分类归纳（如AI、硬件、互联网、创业等）\n" +
                "2. 每个热点用1-2句话概括核心\n" +
                "3. 最后用一句话总结今日科技趋势\n" +
                "4. 中文，500字以内";
        if (instruction != null && !instruction.trim().isEmpty()) {
            prompt += "\n\n额外要求：" + instruction;
        }
        return prompt;
    }

    public String translateText(String text) throws Exception {
        if (text == null || text.trim().isEmpty()) return text;
        boolean hasChinese = text.matches(".*[\\u4e00-\\u9fa5].*");
        if (hasChinese) return text;
        String prompt = "Translate to Chinese, keep it concise, only output the translation:\n" + text;
        return callAi(prompt, 200);
    }

    public String translateNews(String title, String summary) throws Exception {
        boolean titleEn = title != null && !title.matches(".*[\\u4e00-\\u9fa5].*");
        boolean summaryEn = summary != null && !summary.isEmpty() && !summary.matches(".*[\\u4e00-\\u9fa5].*");
        if (!titleEn && !summaryEn) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Translate to Chinese, only output translations:\n");
        if (titleEn) {
            sb.append("Title: ").append(title).append("\n");
        }
        if (summaryEn) {
            sb.append("Summary: ").append(summary.length() > 200 ? summary.substring(0, 200) : summary).append("\n");
        }
        String result = callAi(sb.toString(), 300);
        if (result == null || result.isEmpty()) return null;
        String[] parts = result.split("\n");
        String translatedTitle = title;
        String translatedSummary = summary;
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;
            if (titleEn && (trimmed.startsWith("Title:") || trimmed.startsWith("标题:") || trimmed.startsWith("标题："))) {
                translatedTitle = trimmed.replaceFirst("^(Title:|标题:|标题：)\\s*", "");
            } else if (summaryEn && (trimmed.startsWith("Summary:") || trimmed.startsWith("摘要:") || trimmed.startsWith("摘要："))) {
                translatedSummary = trimmed.replaceFirst("^(Summary:|摘要:|摘要：)\\s*", "");
            } else if (titleEn && translatedTitle.equals(title)) {
                translatedTitle = trimmed;
            } else if (summaryEn && translatedSummary.equals(summary)) {
                translatedSummary = trimmed;
            }
        }
        return translatedTitle + "|||" + translatedSummary;
    }

    private String doPost(String jsonBody, String apiUrl) throws Exception {
        java.net.URL url = new java.net.URL(apiUrl);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);
        conn.setDoOutput(true);
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
        if (is == null) {
            throw new RuntimeException("HTTP " + code + " with no body");
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        conn.disconnect();
        if (code >= 400) {
            throw new RuntimeException("HTTP " + code + ": " + response.toString());
        }
        return response.toString();
    }

    private void doPostStream(String jsonBody, String apiUrl, StreamCallback callback) throws Exception {
        java.net.URL url = new java.net.URL(apiUrl);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(90000);
        conn.setDoOutput(true);
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();
                    if ("[DONE]".equals(data)) {
                        callback.onComplete();
                        conn.disconnect();
                        return;
                    }
                    try {
                        JsonNode chunk = objectMapper.readTree(data);
                        JsonNode choices = chunk.get("choices");
                        if (choices != null && choices.size() > 0) {
                            JsonNode delta = choices.get(0).get("delta");
                            if (delta != null && delta.has("content")) {
                                String content = delta.get("content").asText();
                                if (content != null && !content.isEmpty()) {
                                    callback.onToken(content);
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
        conn.disconnect();
        callback.onComplete();
    }

    private String callAi(String prompt, int maxTokens) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode sysMsg = messages.addObject();
        sysMsg.put("role", "system");
        sysMsg.put("content", SYSTEM_PROMPT_SEARCH);
        ObjectNode userMsg = messages.addObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", maxTokens);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        String apiUrl = baseUrl + "/chat/completions";
        String response = doPost(jsonBody, apiUrl);
        JsonNode root = objectMapper.readTree(response);
        if (root.has("error")) {
            throw new RuntimeException("NVIDIA API error: " + root.get("error").toString());
        }
        JsonNode choices = root.get("choices");
        if (choices != null && choices.size() > 0) {
            return choices.get(0).get("message").get("content").asText().trim();
        }
        return null;
    }

    private ObjectNode buildRequestBody(String systemPrompt, String userPrompt, boolean stream, int maxTokens, double temperature) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode sysMsg = messages.addObject();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        ObjectNode userMsg = messages.addObject();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);
        if (stream) {
            requestBody.put("stream", true);
        }
        return requestBody;
    }

    public String searchNews(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            keyword = keyword.substring(0, MAX_KEYWORD_LENGTH);
        }
        String prompt = buildSearchPrompt(keyword);
        ObjectNode requestBody = buildRequestBody(SYSTEM_PROMPT_SEARCH, prompt, false, 800, 0.5);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        String apiUrl = baseUrl + "/chat/completions";
        String response = doPost(jsonBody, apiUrl);
        JsonNode root = objectMapper.readTree(response);
        if (root.has("error")) {
            throw new RuntimeException("NVIDIA API error: " + root.get("error").toString());
        }
        JsonNode choices = root.get("choices");
        if (choices != null && choices.size() > 0) {
            return choices.get(0).get("message").get("content").asText();
        }
        return "AI未返回有效回答";
    }

    public String hotSummary(String instruction) throws Exception {
        long now = System.currentTimeMillis();
        if (cachedHotSummary != null && (now - cachedHotSummaryTime) < HOT_SUMMARY_CACHE_TTL && (instruction == null || instruction.trim().isEmpty())) {
            return cachedHotSummary;
        }
        String prompt = buildHotSummaryPrompt(instruction);
        ObjectNode requestBody = buildRequestBody(SYSTEM_PROMPT_HOT_SUMMARY, prompt, false, 800, 0.5);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        String apiUrl = baseUrl + "/chat/completions";
        String response = doPost(jsonBody, apiUrl);
        JsonNode root = objectMapper.readTree(response);
        if (root.has("error")) {
            throw new RuntimeException("NVIDIA API error: " + root.get("error").toString());
        }
        JsonNode choices = root.get("choices");
        if (choices != null && choices.size() > 0) {
            String result = choices.get(0).get("message").get("content").asText().trim();
            if (instruction == null || instruction.trim().isEmpty()) {
                cachedHotSummary = result;
                cachedHotSummaryTime = now;
            }
            return result;
        }
        return null;
    }

    public void hotSummaryStream(String instruction, StreamCallback callback) throws Exception {
        String prompt = buildHotSummaryPrompt(instruction);
        ObjectNode requestBody = buildRequestBody(SYSTEM_PROMPT_HOT_SUMMARY, prompt, true, 800, 0.5);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        String apiUrl = baseUrl + "/chat/completions";
        doPostStream(jsonBody, apiUrl, callback);
    }

    public void searchNewsStream(String keyword, StreamCallback callback) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            keyword = keyword.substring(0, MAX_KEYWORD_LENGTH);
        }
        String prompt = buildSearchPrompt(keyword);
        ObjectNode requestBody = buildRequestBody(SYSTEM_PROMPT_SEARCH, prompt, true, 800, 0.5);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        String apiUrl = baseUrl + "/chat/completions";
        doPostStream(jsonBody, apiUrl, callback);
    }
}
