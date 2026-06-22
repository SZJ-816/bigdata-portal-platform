package com.bigdata.portal.cms.controller;

import com.bigdata.portal.common.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 前端API接口 - 新闻相关
 */
@RestController
@RequestMapping("/api")
public class NewsApiController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** 获取新闻列表 */
    @GetMapping("/news")
    public Map<String, Object> getNewsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String keyword) {
        
        int offset = (page - 1) * size;
        StringBuilder sql = new StringBuilder("SELECT * FROM news WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (channel != null && !channel.isEmpty()) {
            sql.append(" AND channel = ?");
            params.add(channel);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (title LIKE ? OR summary LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        
        // 获取总数
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") t";
        Integer total = jdbcTemplate.queryForObject(countSql, params.toArray(), Integer.class);
        
        sql.append(" ORDER BY publish_time DESC LIMIT ?, ?");
        params.add(offset);
        params.add(size);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    /** 获取新闻详情 */
    @GetMapping("/news/{id}")
    public Map<String, Object> getNewsById(@PathVariable Long id) {
        String sql = "SELECT * FROM news WHERE id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        
        Map<String, Object> result = new HashMap<>();
        if (list.isEmpty()) {
            result.put("success", false);
            result.put("error", "新闻不存在");
        } else {
            // 更新浏览量
            jdbcTemplate.update("UPDATE news SET view_count = IFNULL(view_count, 0) + 1 WHERE id = ?", id);
            result.put("success", true);
            result.put("data", list.get(0));
        }
        return result;
    }

    /** 获取热门新闻 */
    @GetMapping("/news/hot")
    public Map<String, Object> getHotNews(@RequestParam(defaultValue = "10") int size) {
        String sql = "SELECT * FROM news ORDER BY view_count DESC LIMIT ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    /** 获取频道列表 */
    @GetMapping("/channel")
    public Map<String, Object> getChannels() {
        String sql = "SELECT DISTINCT channel, COUNT(*) as count FROM news GROUP BY channel ORDER BY count DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    /** 获取各频道新闻 */
    @GetMapping("/news/by-channel")
    public Map<String, Object> getNewsByChannel(@RequestParam(defaultValue = "4") int size) {
        String channelSql = "SELECT DISTINCT channel FROM news";
        List<Map<String, Object>> channels = jdbcTemplate.queryForList(channelSql);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map<String, Object> ch : channels) {
            String channel = (String) ch.get("channel");
            String newsSql = "SELECT * FROM news WHERE channel = ? ORDER BY publish_time DESC LIMIT ?";
            List<Map<String, Object>> news = jdbcTemplate.queryForList(newsSql, channel, size);
            
            Map<String, Object> item = new HashMap<>();
            item.put("channel", channel);
            item.put("news", news);
            data.add(item);
        }
        result.put("data", data);
        return result;
    }

    /** 获取相关新闻 */
    @GetMapping("/news/{id}/related")
    public Map<String, Object> getRelatedNews(@PathVariable Long id, @RequestParam(defaultValue = "5") int size) {
        String channelSql = "SELECT channel FROM news WHERE id = ?";
        List<Map<String, Object>> channelResult = jdbcTemplate.queryForList(channelSql, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        
        if (channelResult.isEmpty()) {
            result.put("data", Collections.emptyList());
            return result;
        }
        
        String channel = (String) channelResult.get(0).get("channel");
        String sql = "SELECT * FROM news WHERE channel = ? AND id != ? ORDER BY RAND() LIMIT ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, channel, id, size);
        result.put("data", list);
        return result;
    }
}
