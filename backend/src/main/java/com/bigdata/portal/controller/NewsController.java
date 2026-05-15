package com.bigdata.portal.controller;

import com.bigdata.portal.entity.Comment;
import com.bigdata.portal.entity.News;
import com.bigdata.portal.service.AiService;
import com.bigdata.portal.service.NewsService;
import com.bigdata.portal.service.RssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private RssService rssService;

    @Autowired
    private AiService aiService;

    @GetMapping
    public Map<String, Object> getList(@RequestParam(required = false) String channel,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        if (keyword != null && !keyword.isEmpty()) {
            return newsService.search(keyword, page, size);
        } else if (channel != null && !channel.isEmpty()) {
            return newsService.getByChannel(channel, page, size);
        } else {
            return newsService.getList(page, size);
        }
    }

    @GetMapping("/breaking")
    public Map<String, Object> getBreaking() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", newsService.getBreaking());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        News news = newsService.getById(id);
        if (news == null) {
            result.put("data", null);
            result.put("success", false);
            result.put("message", "新闻不存在");
        } else {
            result.put("data", news);
            result.put("success", true);
        }
        return result;
    }

    @PostMapping("/{id}/comment")
    public Map<String, Object> addComment(@PathVariable Long id,
                                          @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Object userIdObj = params.get("userId");
        Object contentObj = params.get("content");
        if (userIdObj == null || contentObj == null || contentObj.toString().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "参数不完整");
            return result;
        }
        Long userId = Long.valueOf(userIdObj.toString());
        String content = contentObj.toString();
        newsService.addComment(id, userId, content);
        result.put("success", true);
        return result;
    }

    @GetMapping("/{id}/comments")
    public Map<String, Object> getComments(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", newsService.getCommentsByNewsId(id));
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            News news = newsService.getById(id);
            if (news == null) {
                result.put("success", false);
                result.put("message", "新闻不存在");
                return result;
            }
            if (params.containsKey("summary")) {
                news.setSummary(params.get("summary").toString());
            }
            if (params.containsKey("imageUrl")) {
                news.setImageUrl(params.get("imageUrl").toString());
            }
            newsService.update(news);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/refresh")
    public Map<String, Object> refreshNews() {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = rssService.fetchAndSave();
            result.put("success", true);
            result.put("data", count);
            result.put("message", "成功抓取 " + count + " 条新闻");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "抓取失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/translate")
    public Map<String, Object> translate(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            News news = newsService.getById(id);
            if (news == null) {
                result.put("success", false);
                result.put("message", "新闻不存在");
                return result;
            }
            String translated = aiService.translateNews(news.getTitle(), news.getSummary());
            if (translated != null) {
                result.put("data", translated);
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "无需翻译或翻译失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "翻译失败: " + e.getMessage());
        }
        return result;
    }
}
