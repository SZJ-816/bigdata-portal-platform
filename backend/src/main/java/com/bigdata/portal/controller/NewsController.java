package com.bigdata.portal.controller;

import com.bigdata.portal.config.JwtConfig;
import com.bigdata.portal.entity.Comment;
import com.bigdata.portal.entity.News;
import com.bigdata.portal.service.AiService;
import com.bigdata.portal.service.NewsService;
import com.bigdata.portal.service.RssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private JwtConfig jwtConfig;

    @GetMapping
    public Map<String, Object> getList(@RequestParam(required = false) String channel,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        if (page < 1) page = 1;
        if (size < 1 || size > 50) size = 10;
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
    public Map<String, Object> getById(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = null;
        String token = jwtConfig.extractToken(request.getHeader("Authorization"));
        if (token != null && jwtConfig.validateToken(token)) {
            userId = jwtConfig.getUserIdFromToken(token);
        }
        News news = newsService.getById(id, userId);
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

    @GetMapping("/hot")
    public Map<String, Object> getHot() {
        Map<String, Object> result = new HashMap<>();
        List<News> hotNews = newsService.getHot(10);
        result.put("data", hotNews);
        return result;
    }

    @PostMapping("/{id}/comment")
    public Map<String, Object> addComment(@PathVariable Long id,
                                          @RequestBody Map<String, Object> params,
                                          HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        Object contentObj = params.get("content");
        if (contentObj == null || contentObj.toString().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "评论内容不能为空");
            return result;
        }
        String content = contentObj.toString();
        if (content.length() > 1000) {
            result.put("error", "评论内容不能超过1000字");
            result.put("success", false);
            return result;
        }
        Long userId = ((Number) userIdObj).longValue();
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
            News news = newsService.getById(id, null);
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
            News news = newsService.getById(id, null);
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
