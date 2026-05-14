package com.bigdata.portal.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @GetMapping("/realtime")
    public Map<String, Object> getRealtimeStats() {
        Map<String, Object> result = new HashMap<>();
        Random random = new Random();
        Map<String, Object> data = new HashMap<>();
        data.put("todayUV", 3000 + random.nextInt(2000));
        data.put("todayPV", 25000 + random.nextInt(8000));
        data.put("onlineUsers", 500 + random.nextInt(300));
        data.put("avgDuration", 180 + random.nextInt(180));
        result.put("data", data);
        return result;
    }

    @GetMapping("/trend")
    public Map<String, Object> getTrend() {
        Map<String, Object> result = new HashMap<>();
        Random random = new Random();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("hour", i + ":00");
            item.put("pv", 500 + random.nextInt(2000));
            item.put("uv", 100 + random.nextInt(500));
            list.add(item);
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/hot-news")
    public Map<String, Object> getHotNews() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        String[] names = {"GPT-5正式发布", "豆包AI月活破2亿", "月之暗面10亿融资", "Blackwell Ultra量产", "Spark 4.0发布", "通义千问3.0", "AlphaFold 4", "Flink 2.0", "Azure AI Agent", "Graviton5发布"};
        int[] views = {5842, 4123, 2890, 3456, 2876, 2543, 3215, 2341, 2109, 1987};
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", names[i]);
            item.put("value", views[i]);
            list.add(item);
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/channel-dist")
    public Map<String, Object> getChannelDist() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        String[] channels = {"AI", "大数据", "云计算", "互联网", "硬件", "创业"};
        int[] values = {9057, 6784, 6639, 5999, 5110, 4124};
        for (int i = 0; i < channels.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", channels[i]);
            item.put("value", values[i]);
            list.add(item);
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/region-dist")
    public Map<String, Object> getRegionDist() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        String[] regions = {"北京", "上海", "深圳", "杭州", "广州", "成都", "武汉", "南京", "西安", "重庆"};
        int[] values = {3847, 3210, 2876, 2654, 1987, 1654, 1432, 1287, 1098, 987};
        for (int i = 0; i < regions.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", regions[i]);
            item.put("value", values[i]);
            list.add(item);
        }
        result.put("data", list);
        return result;
    }

    @GetMapping("/funnel")
    public Map<String, Object> getFunnel() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        String[] names = {"浏览", "点击", "阅读", "评论", "分享"};
        int[] values = {100, 68, 42, 18, 8};
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", names[i]);
            item.put("value", values[i]);
            list.add(item);
        }
        result.put("data", list);
        return result;
    }
}
