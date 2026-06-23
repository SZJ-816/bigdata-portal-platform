package com.bigdata.portal.ai.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.ai.service.AiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

/**
 * AI接口Controller
 * 提供文章摘要生成、标签生成、AI聊天、图片生成等能力
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * 生成文章摘要
     */
    @PostMapping("/summary")
    @OperationLog(module = "AI智能", type = "生成", description = "生成文章摘要")
    public R<Map<String, String>> generateSummary(@RequestParam String content,
                                                   @RequestParam(required = false, defaultValue = "200") Integer maxLength) {
        String summary = aiService.generateSummary(content, maxLength);
        Map<String, String> result = new HashMap<>();
        result.put("summary", summary);
        return R.ok(result);
    }

    /**
     * 生成文章标签
     */
    @PostMapping("/tags")
    @OperationLog(module = "AI智能", type = "生成", description = "生成文章标签")
    public R<Map<String, String>> generateTags(@RequestParam String content) {
        String tags = aiService.generateTags(content);
        Map<String, String> result = new HashMap<>();
        result.put("tags", tags);
        return R.ok(result);
    }

    /**
     * AI聊天接口
     */
    @PostMapping("/chat")
    @OperationLog(module = "AI智能", type = "聊天", description = "AI对话")
    public R<Map<String, String>> chat(@RequestParam String message,
                                       @RequestParam(required = false) String context) {
        String reply = aiService.chat(message, context);
        Map<String, String> result = new HashMap<>();
        result.put("reply", reply);
        return R.ok(result);
    }

    /**
     * AI图片生成接口
     */
    @PostMapping("/image")
    @OperationLog(module = "AI智能", type = "生成", description = "AI图片生成")
    public R<Map<String, String>> generateImage(@RequestParam String prompt,
                                                  @RequestParam(required = false, defaultValue = "1024") Integer width,
                                                  @RequestParam(required = false, defaultValue = "1024") Integer height) {
        String imageUrl = aiService.generateImage(prompt, width, height);
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", imageUrl);
        return R.ok(result);
    }

    /**
     * AI智能搜索新闻
     */
    @GetMapping("/search")
    @OperationLog(module = "AI智能", type = "搜索", description = "AI智能搜索新闻")
    public R<Map<String, String>> search(@RequestParam String keyword) {
        String result = aiService.searchNews(keyword);
        Map<String, String> data = new HashMap<>();
        data.put("result", result);
        return R.ok(data);
    }

    /**
     * AI智能搜索新闻（SSE流式）
     */
    @GetMapping("/search/stream")
    public SseEmitter searchStream(@RequestParam String keyword) {
        return aiService.searchNewsStream(keyword);
    }

    /**
     * AI热点总结
     */
    @GetMapping("/hot-summary")
    @OperationLog(module = "AI智能", type = "总结", description = "AI热点总结")
    public R<Map<String, String>> hotSummary(@RequestParam(required = false) String instruction) {
        String result = aiService.hotSummary(instruction);
        Map<String, String> data = new HashMap<>();
        data.put("result", result);
        return R.ok(data);
    }

    /**
     * AI热点总结（SSE流式）
     */
    @GetMapping("/hot-summary/stream")
    public SseEmitter hotSummaryStream(@RequestParam(required = false) String instruction) {
        return aiService.hotSummaryStream(instruction);
    }

    /**
     * AI智能翻译
     */
    @GetMapping("/translate")
    @OperationLog(module = "AI智能", type = "翻译", description = "AI智能翻译")
    public R<Map<String, String>> translate(@RequestParam String text) {
        String result = aiService.translate(text);
        Map<String, String> data = new HashMap<>();
        data.put("result", result);
        return R.ok(data);
    }
}
