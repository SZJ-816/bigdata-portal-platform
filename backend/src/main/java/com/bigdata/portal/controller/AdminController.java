package com.bigdata.portal.controller;

import com.bigdata.portal.entity.News;
import com.bigdata.portal.entity.User;
import com.bigdata.portal.mapper.NewsMapper;
import com.bigdata.portal.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/news")
    public Map<String, Object> listNews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        int offset = (page - 1) * size;
        List<News> list;
        long total;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = newsMapper.searchPage(keyword, offset, size);
            total = newsMapper.countByKeyword(keyword);
        } else if (channel != null && !channel.trim().isEmpty()) {
            list = newsMapper.findByChannelPage(channel, offset, size);
            total = newsMapper.countByChannel(channel);
        } else {
            list = newsMapper.findPage(offset, size);
            total = newsMapper.count();
        }
        result.put("data", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("success", true);
        return result;
    }

    @GetMapping("/news/{id}")
    public Map<String, Object> getNews(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        News news = newsMapper.findById(id);
        if (news != null) {
            result.put("data", news);
            result.put("success", true);
        } else {
            result.put("error", "新闻不存在");
            result.put("success", false);
        }
        return result;
    }

    @PutMapping("/news/{id}")
    public Map<String, Object> updateNews(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        News news = newsMapper.findById(id);
        if (news == null) {
            result.put("error", "新闻不存在");
            result.put("success", false);
            return result;
        }
        if (params.containsKey("title")) news.setTitle((String) params.get("title"));
        if (params.containsKey("summary")) news.setSummary((String) params.get("summary"));
        if (params.containsKey("channel")) news.setChannel((String) params.get("channel"));
        if (params.containsKey("source")) news.setSource((String) params.get("source"));
        if (params.containsKey("imageUrl")) news.setImageUrl((String) params.get("imageUrl"));
        if (params.containsKey("isBreaking")) news.setIsBreaking(((Number) params.get("isBreaking")).intValue());
        newsMapper.updateFull(news);
        result.put("success", true);
        return result;
    }

    @DeleteMapping("/news/{id}")
    public Map<String, Object> deleteNews(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        newsMapper.deleteById(id);
        result.put("success", true);
        return result;
    }

    @GetMapping("/users")
    public Map<String, Object> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> result = new HashMap<>();
        int offset = (page - 1) * size;
        List<User> list = userMapper.findPage(offset, size);
        long total = userMapper.count();
        result.put("data", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("success", true);
        return result;
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.findById(id);
        if (user == null) {
            result.put("error", "用户不存在");
            result.put("success", false);
            return result;
        }
        userMapper.deleteById(id);
        result.put("success", true);
        return result;
    }

    @GetMapping("/channels")
    public Map<String, Object> listChannels() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = newsMapper.countByChannelGroup();
        result.put("data", list);
        result.put("success", true);
        return result;
    }
}
