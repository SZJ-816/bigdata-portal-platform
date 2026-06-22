package com.bigdata.portal.ai.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.ai.service.AiService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI接口Controller
 * 提供文章摘要生成、标签生成等AI能力
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
     *
     * @param content   文章内容
     * @param maxLength 摘要最大长度
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
     *
     * @param content 文章内容
     */
    @PostMapping("/tags")
    @OperationLog(module = "AI智能", type = "生成", description = "生成文章标签")
    public R<Map<String, String>> generateTags(@RequestParam String content) {
        String tags = aiService.generateTags(content);
        Map<String, String> result = new HashMap<>();
        result.put("tags", tags);
        return R.ok(result);
    }
}
