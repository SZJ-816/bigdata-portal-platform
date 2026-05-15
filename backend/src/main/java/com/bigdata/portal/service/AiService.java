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
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public interface StreamCallback {
        void onToken(String token);
        void onComplete();
        void onError(Exception e);
    }

    private String buildContext(String keyword) {
        List<News> searchResults = newsMapper.search(keyword);
        List<News> channelResults = newsMapper.findByChannelTop20(keyword);
        if (searchResults.size() < 3 && channelResults != null) {
            for (News n : channelResults) {
                if (searchResults.stream().noneMatch(s -> s.getId().equals(n.getId()))) {
                    searchResults.add(n);
                }
                if (searchResults.size() >= 3) break;
            }
        }
        if (searchResults.isEmpty()) {
            return "无相关新闻";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (News news : searchResults) {
            if (count >= 3) break;
            sb.append(count + 1).append(".");
            sb.append(news.getTitle());
            if (news.getSummary() != null && !news.getSummary().isEmpty()) {
                String summary = news.getSummary();
                if (summary.length() > 80) {
                    summary = summary.substring(0, 80);
                }
                sb.append(" - ").append(summary);
            }
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }

    private String buildPrompt(String keyword, String context) {
        return "你是科技资讯助手。根据以下新闻简要回答用户问题，200字以内，中文。\n新闻：" + context + "\n问题：" + keyword;
    }

    private String buildHotSummaryContext() {
        List<News> allNews = newsMapper.findTop20();
        if (allNews == null || allNews.isEmpty()) {
            return "暂无新闻";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (News news : allNews) {
            if (count >= 10) break;
            sb.append(count + 1).append(".");
            sb.append(news.getTitle());
            if (news.getSummary() != null && !news.getSummary().isEmpty()) {
                String summary = news.getSummary();
                if (summary.length() > 60) {
                    summary = summary.substring(0, 60);
                }
                sb.append(" - ").append(summary);
            }
            sb.append(" [").append(news.getChannel() != null ? news.getChannel() : "").append("]");
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }

    private String buildHotSummaryPrompt(String instruction) {
        String context = buildHotSummaryContext();
        String basePrompt = "你是科技资讯编辑。根据以下今日新闻，生成今日热点总结，中文，300字以内。\n今日新闻：\n" + context;
        if (instruction != null && !instruction.trim().isEmpty()) {
            basePrompt += "\n额外要求：" + instruction;
        }
        return basePrompt;
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

    private String callAi(String prompt, int maxTokens) throws Exception {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode userMsg = messages.addObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", maxTokens);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        Path tempFile = writeTempJson(jsonBody);
        try {
            String apiUrl = baseUrl + "/chat/completions";
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-s", "-X", "POST", apiUrl,
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + apiKey,
                    "--connect-timeout", "15",
                    "--max-time", "30",
                    "-d", "@" + tempFile.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("curl exited with code " + exitCode);
            }
            JsonNode root = objectMapper.readTree(response.toString());
            if (root.has("error")) {
                throw new RuntimeException("NVIDIA API error: " + root.get("error").toString());
            }
            JsonNode choices = root.get("choices");
            if (choices != null && choices.size() > 0) {
                return choices.get(0).get("message").get("content").asText().trim();
            }
            return null;
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private ObjectNode buildRequestBody(String prompt, boolean stream) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        ArrayNode messages = requestBody.putArray("messages");
        ObjectNode userMsg = messages.addObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        requestBody.put("temperature", 0.6);
        requestBody.put("max_tokens", 512);
        if (stream) {
            requestBody.put("stream", true);
        }
        return requestBody;
    }

    private Path writeTempJson(String jsonBody) throws Exception {
        Path tempFile = Files.createTempFile("nvidia_req_", ".json");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile.toFile()), StandardCharsets.UTF_8)) {
            writer.write(jsonBody);
        }
        return tempFile;
    }

    public String searchNews(String keyword) throws Exception {
        String context = buildContext(keyword);
        String prompt = buildPrompt(keyword, context);
        ObjectNode requestBody = buildRequestBody(prompt, false);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        Path tempFile = writeTempJson(jsonBody);
        try {
            String apiUrl = baseUrl + "/chat/completions";
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-s", "-X", "POST", apiUrl,
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + apiKey,
                    "--connect-timeout", "15",
                    "--max-time", "30",
                    "-d", "@" + tempFile.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("curl exited with code " + exitCode + ": " + response.toString());
            }
            JsonNode root = objectMapper.readTree(response.toString());
            if (root.has("error")) {
                throw new RuntimeException("NVIDIA API error: " + root.get("error").toString());
            }
            JsonNode choices = root.get("choices");
            if (choices != null && choices.size() > 0) {
                return choices.get(0).get("message").get("content").asText();
            }
            return "AI未返回有效回答";
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public String hotSummary(String instruction) throws Exception {
        String prompt = buildHotSummaryPrompt(instruction);
        return callAi(prompt, 512);
    }

    public void hotSummaryStream(String instruction, StreamCallback callback) throws Exception {
        String prompt = buildHotSummaryPrompt(instruction);
        ObjectNode requestBody = buildRequestBody(prompt, true);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        Path tempFile = writeTempJson(jsonBody);
        try {
            String apiUrl = baseUrl + "/chat/completions";
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-s", "-N", "-X", "POST", apiUrl,
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + apiKey,
                    "--connect-timeout", "15",
                    "--max-time", "60",
                    "-d", "@" + tempFile.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            callback.onComplete();
                            process.waitFor();
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
            process.waitFor();
            callback.onComplete();
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public void searchNewsStream(String keyword, StreamCallback callback) throws Exception {
        String context = buildContext(keyword);
        String prompt = buildPrompt(keyword, context);
        ObjectNode requestBody = buildRequestBody(prompt, true);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        Path tempFile = writeTempJson(jsonBody);
        try {
            String apiUrl = baseUrl + "/chat/completions";
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-s", "-N", "-X", "POST", apiUrl,
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + apiKey,
                    "--connect-timeout", "15",
                    "--max-time", "30",
                    "-d", "@" + tempFile.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            callback.onComplete();
                            process.waitFor();
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
            process.waitFor();
            callback.onComplete();
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
