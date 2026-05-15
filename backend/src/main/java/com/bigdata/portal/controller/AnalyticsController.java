package com.bigdata.portal.controller;

import com.bigdata.portal.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserHistoryMapper historyMapper;

    @Autowired
    private CommentMapper commentMapper;

    @GetMapping("/realtime")
    public Map<String, Object> getRealtimeStats() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("todayUV", behaviorMapper.countTodayUV());
        data.put("todayPV", behaviorMapper.countTodayPV());
        data.put("onlineUsers", behaviorMapper.countOnlineUsers());
        data.put("avgDuration", historyMapper.avgDurationToday());
        data.put("totalUsers", userMapper.count());
        data.put("totalNews", newsMapper.count());
        data.put("todayNews", newsMapper.countToday());
        data.put("totalViews", newsMapper.sumViewCount() != null ? newsMapper.sumViewCount() : 0);
        result.put("data", data);
        return result;
    }

    @GetMapping("/trend")
    public Map<String, Object> getTrend() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> raw = behaviorMapper.trendByHourToday();
        List<Map<String, Object>> list = new ArrayList<>();
        Set<Integer> existingHours = new HashSet<>();
        for (Map<String, Object> r : raw) {
            Map<String, Object> item = new HashMap<>();
            int hour = ((Number) r.get("hour")).intValue();
            existingHours.add(hour);
            item.put("hour", hour + ":00");
            item.put("uv", r.get("uv"));
            item.put("pv", r.get("pv"));
            list.add(item);
        }
        for (int i = 0; i < 24; i++) {
            if (!existingHours.contains(i)) {
                Map<String, Object> item = new HashMap<>();
                item.put("hour", i + ":00");
                item.put("uv", 0);
                item.put("pv", 0);
                list.add(i, item);
            }
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/hot-news")
    public Map<String, Object> getHotNews() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> raw = newsMapper.findTopByViews(10);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> r : raw) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", r.get("title"));
            item.put("value", r.get("viewCount"));
            list.add(item);
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/channel-dist")
    public Map<String, Object> getChannelDist() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> raw = newsMapper.countByChannelGroup();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> r : raw) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", r.get("channel"));
            item.put("value", r.get("cnt"));
            list.add(item);
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/region-dist")
    public Map<String, Object> getRegionDist() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = behaviorMapper.countByEventTypeToday();
        result.put("data", list);
        return result;
    }

    @GetMapping("/funnel")
    public Map<String, Object> getFunnel() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> raw = behaviorMapper.countByEventTypeToday();
        Map<String, Long> eventMap = new LinkedHashMap<>();
        for (Map<String, Object> r : raw) {
            String eventType = (String) r.get("event_type");
            long cnt = ((Number) r.get("cnt")).longValue();
            eventMap.put(eventType, cnt);
        }
        String[] funnelOrder = {"view", "click", "read", "comment", "share", "favorite"};
        String[] funnelLabels = {"浏览", "点击", "阅读", "评论", "分享", "收藏"};
        long maxVal = eventMap.values().stream().max(Long::compare).orElse(1L);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < funnelOrder.length; i++) {
            long val = eventMap.getOrDefault(funnelOrder[i], 0L);
            if (val > 0 || i == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", funnelLabels[i]);
                item.put("value", maxVal > 0 ? Math.round(val * 100.0 / maxVal) : 0);
                list.add(item);
            }
        }
        if (list.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("name", "浏览");
            empty.put("value", 0);
            list.add(empty);
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userMapper.count());
        data.put("todayUsers", userMapper.countToday());
        data.put("totalNews", newsMapper.count());
        data.put("todayNews", newsMapper.countToday());
        data.put("totalViews", newsMapper.sumViewCount() != null ? newsMapper.sumViewCount() : 0);
        data.put("totalBehaviors", behaviorMapper.countTotalBehaviors());
        data.put("totalReadHistory", historyMapper.countTotal());
        result.put("data", data);
        return result;
    }
}
