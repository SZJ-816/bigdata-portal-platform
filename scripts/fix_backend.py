#!/usr/bin/env python3
"""修复频道分布图、漏斗图、行为分布图的数据问题"""
import subprocess

CH_SERVICE_PATH = "/root/bigdata-portal-platform/backend/src/main/java/com/bigdata/portal/service/ClickHouseAnalyticsService.java"
CONTROLLER_PATH = "/root/bigdata-portal-platform/backend/src/main/java/com/bigdata/portal/controller/AnalyticsController.java"

# 修复 ClickHouseAnalyticsService.java: getChannelDist - 改为从MySQL news表统计频道
# 修复 getFunnel - 使用正确的事件类型名称
# 修复 getRegionDist - 返回中文事件名

# 读取当前文件
with open(CH_SERVICE_PATH, 'r', encoding='utf-8') as f:
    ch_content = f.read()

# 替换 getChannelDist 方法 - 从MySQL news表直接统计
old_channel = '''    public List<Map<String, Object>> getChannelDist(String range) {
        if (chAvailable) {
            try {
                String dateFilter = buildDateFilter(range);
                return chJdbcTemplate.queryForList(
                        "SELECT n.channel as name, COUNT(*) as cnt FROM behavior_events be " +
                        "JOIN news n ON be.target_id = n.id AND be.target_type = 'news' " +
                        "WHERE " + dateFilter + " AND n.channel IS NOT NULL AND n.channel != '' " +
                        "GROUP BY n.channel ORDER BY cnt DESC");
            } catch (Exception e) {
                log.warn("ClickHouse channel dist query failed, falling back to MySQL: {}", e.getMessage());
                chAvailable = false;
            }
        }
        return mysqlJdbcTemplate.queryForList(
                "SELECT n.channel as name, COUNT(*) as cnt FROM user_behavior ub " +
                "JOIN news n ON ub.target_id = n.id AND ub.target_type = 'news' " +
                "WHERE " + buildMySQLDateFilter(range) + " AND n.channel IS NOT NULL AND n.channel != '' " +
                "GROUP BY n.channel ORDER BY cnt DESC");
    }'''

new_channel = '''    public List<Map<String, Object>> getChannelDist(String range) {
        // 优先从MySQL news表直接统计新闻的频道分布（真实新闻数据）
        List<Map<String, Object>> newsChannelDist = mysqlJdbcTemplate.queryForList(
                "SELECT channel, COUNT(*) as cnt, COALESCE(SUM(view_count), 0) as views FROM news " +
                "WHERE channel IS NOT NULL AND channel != '' GROUP BY channel ORDER BY cnt DESC");

        // 如果ClickHouse有用户行为数据，也把真实浏览量叠加进去
        if (chAvailable) {
            try {
                String dateFilter = buildDateFilter(range);
                List<Map<String, Object>> chData = chJdbcTemplate.queryForList(
                        "SELECT n.channel, COUNT(*) as view_cnt FROM behavior_events be " +
                        "JOIN news n ON be.target_id = n.id " +
                        "WHERE " + dateFilter + " AND n.channel IS NOT NULL AND n.channel != '' " +
                        "GROUP BY n.channel");
                Map<String, Long> chMap = new HashMap<>();
                for (Map<String, Object> row : chData) {
                    String ch = (String) row.get("channel");
                    Long vc = ((Number) row.get("view_cnt")).longValue();
                    if (ch != null) chMap.put(ch, vc);
                }
                for (Map<String, Object> row : newsChannelDist) {
                    String ch = (String) row.get("channel");
                    long baseViews = ((Number) row.get("views")).longValue();
                    long chViews = chMap.getOrDefault(ch, 0L);
                    row.put("views", Math.max(baseViews, chViews));
                }
            } catch (Exception e) {
                log.warn("ClickHouse channel dist query failed: {}", e.getMessage());
            }
        }
        return newsChannelDist;
    }'''

ch_content = ch_content.replace(old_channel, new_channel)

# 替换 getFunnel 方法 - 使用正确的事件类型名称映射
old_funnel = '''    public List<Map<String, Object>> getFunnel(String range) {
        if (chAvailable) {
            try {
                String dateFilter = buildDateFilter(range);
                return chJdbcTemplate.queryForList(
                        "SELECT event_type, COUNT(*) as cnt FROM behavior_events WHERE " + dateFilter + " GROUP BY event_type ORDER BY cnt DESC LIMIT 10");
            } catch (Exception e) {
                log.warn("ClickHouse funnel query failed, falling back to MySQL: {}", e.getMessage());
                chAvailable = false;
            }
        }
        return mysqlJdbcTemplate.queryForList(
                "SELECT event_type, COUNT(*) as cnt FROM user_behavior WHERE " + buildMySQLDateFilter(range) + " GROUP BY event_type ORDER BY cnt DESC LIMIT 10");
    }'''

new_funnel = '''    public List<Map<String, Object>> getFunnel(String range) {
        if (chAvailable) {
            try {
                String dateFilter = buildDateFilter(range);
                return chJdbcTemplate.queryForList(
                        "SELECT event_type, COUNT(*) as cnt FROM user_behavior WHERE " + dateFilter + " GROUP BY event_type ORDER BY cnt DESC LIMIT 10");
            } catch (Exception e) {
                log.warn("ClickHouse funnel query failed, falling back to MySQL: {}", e.getMessage());
                chAvailable = false;
            }
        }
        return mysqlJdbcTemplate.queryForList(
                "SELECT event_type, COUNT(*) as cnt FROM user_behavior WHERE " + buildMySQLDateFilter(range) + " GROUP BY event_type ORDER BY cnt DESC LIMIT 10");
    }'''

ch_content = ch_content.replace(old_funnel, new_funnel)

# 替换 getRegionDist 方法 - 使用正确的事件类型
old_region = '''    public List<Map<String, Object>> getRegionDist(String range) {
        if (chAvailable) {
            try {
                String dateFilter = buildDateFilter(range);
                return chJdbcTemplate.queryForList(
                        "SELECT event_type, COUNT(*) as cnt FROM behavior_events WHERE " + dateFilter + " GROUP BY event_type ORDER BY cnt DESC");
            } catch (Exception e) {
                log.warn("ClickHouse region dist query failed, falling back to MySQL: {}", e.getMessage());
                chAvailable = false;
            }
        }
        return mysqlJdbcTemplate.queryForList(
                "SELECT event_type, COUNT(*) as cnt FROM user_behavior WHERE " + buildMySQLDateFilter(range) + " GROUP BY event_type ORDER BY cnt DESC");
    }'''

new_region = '''    public List<Map<String, Object>> getRegionDist(String range) {
        if (chAvailable) {
            try {
                String dateFilter = buildDateFilter(range);
                return chJdbcTemplate.queryForList(
                        "SELECT event_type, COUNT(*) as cnt FROM user_behavior WHERE " + dateFilter + " GROUP BY event_type ORDER BY cnt DESC");
            } catch (Exception e) {
                log.warn("ClickHouse region dist query failed, falling back to MySQL: {}", e.getMessage());
                chAvailable = false;
            }
        }
        return mysqlJdbcTemplate.queryForList(
                "SELECT event_type, COUNT(*) as cnt FROM user_behavior WHERE " + buildMySQLDateFilter(range) + " GROUP BY event_type ORDER BY cnt DESC");
    }'''

ch_content = ch_content.replace(old_region, new_region)

# 保存修改
with open(CH_SERVICE_PATH, 'w', encoding='utf-8') as f:
    f.write(ch_content)

print("✅ ClickHouseAnalyticsService.java 已修复")

# --- 修复 AnalyticsController.java ---
with open(CONTROLLER_PATH, 'r', encoding='utf-8') as f:
    ctrl_content = f.read()

# 修复 getChannelDist - 正确读取字段名(channel/cnt/views)
old_ctrl_channel = '''    @GetMapping("/channel-dist")
    public ApiResponse<List<Map<String, Object>>> getChannelDist(@RequestParam(defaultValue = "today") String range) {
        List<Map<String, Object>> raw = chAnalyticsService.getChannelDist();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> r2 : raw) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", r2.get("channel"));
            item.put("value", r2.get("cnt"));
            list.add(item);
        }
        return ApiResponse.success(list);
    }'''

new_ctrl_channel = '''    @GetMapping("/channel-dist")
    public ApiResponse<List<Map<String, Object>>> getChannelDist(@RequestParam(defaultValue = "today") String range) {
        List<Map<String, Object>> raw = chAnalyticsService.getChannelDist(normalizeRange(range));
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> r2 : raw) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", r2.get("channel"));
            // 优先使用真实views数，没有则用新闻数作为计数
            Object views = r2.get("views");
            Object cnt = r2.get("cnt");
            long value = 0;
            if (views instanceof Number) value = ((Number) views).longValue();
            else if (cnt instanceof Number) value = ((Number) cnt).longValue();
            item.put("value", value);
            list.add(item);
        }
        return ApiResponse.success(list);
    }'''

ctrl_content = ctrl_content.replace(old_ctrl_channel, new_ctrl_channel)

# 修复 getFunnel - 使用实际的事件类型名称映射
old_ctrl_funnel = '''    @GetMapping("/funnel")
    public ApiResponse<List<Map<String, Object>>> getFunnel(@RequestParam(defaultValue = "today") String range) {
        String r = normalizeRange(range);
        List<Map<String, Object>> raw = chAnalyticsService.getRegionDist(r);
        Map<String, Long> eventMap = new LinkedHashMap<>();
        for (Map<String, Object> r2 : raw) {
            String eventType = (String) r2.get("event_type");
            long cnt = ((Number) r2.get("cnt")).longValue();
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
        return ApiResponse.success(list);
    }'''

new_ctrl_funnel = '''    @GetMapping("/funnel")
    public ApiResponse<List<Map<String, Object>>> getFunnel(@RequestParam(defaultValue = "today") String range) {
        String r = normalizeRange(range);
        List<Map<String, Object>> raw = chAnalyticsService.getFunnel(r);
        Map<String, Long> eventMap = new LinkedHashMap<>();
        for (Map<String, Object> r2 : raw) {
            String eventType = (String) r2.get("event_type");
            long cnt = ((Number) r2.get("cnt")).longValue();
            eventMap.put(eventType, cnt);
        }
        // 实际事件类型到标签的映射
        Map<String, String> eventLabels = new LinkedHashMap<>();
        eventLabels.put("page_view", "浏览");
        eventLabels.put("news_click", "点击");
        eventLabels.put("news_read", "阅读");
        eventLabels.put("news_comment", "评论");
        eventLabels.put("news_share", "分享");
        eventLabels.put("search", "搜索");
        eventLabels.put("news_favorite", "收藏");

        long maxVal = eventMap.values().stream().max(Long::compare).orElse(1L);
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : eventLabels.entrySet()) {
            long val = eventMap.getOrDefault(entry.getKey(), 0L);
            if (val > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getValue());
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
        return ApiResponse.success(list);
    }'''

ctrl_content = ctrl_content.replace(old_ctrl_funnel, new_ctrl_funnel)

# 修复 getRegionDist - 中文事件名映射
old_ctrl_region = '''    @GetMapping("/region-dist")
    public ApiResponse<List<Map<String, Object>>> getRegionDist(@RequestParam(defaultValue = "today") String range) {
        return ApiResponse.success(chAnalyticsService.getRegionDist(normalizeRange(range)));
    }'''

new_ctrl_region = '''    @GetMapping("/region-dist")
    public ApiResponse<List<Map<String, Object>>> getRegionDist(@RequestParam(defaultValue = "today") String range) {
        List<Map<String, Object>> raw = chAnalyticsService.getRegionDist(normalizeRange(range));
        Map<String, String> eventLabels = new LinkedHashMap<>();
        eventLabels.put("page_view", "浏览");
        eventLabels.put("news_click", "点击");
        eventLabels.put("news_read", "阅读");
        eventLabels.put("news_comment", "评论");
        eventLabels.put("news_share", "分享");
        eventLabels.put("search", "搜索");
        eventLabels.put("news_favorite", "收藏");
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> r2 : raw) {
            String eventType = (String) r2.get("event_type");
            long cnt = ((Number) r2.get("cnt")).longValue();
            Map<String, Object> item = new HashMap<>();
            item.put("name", eventLabels.getOrDefault(eventType, eventType));
            item.put("value", cnt);
            list.add(item);
        }
        return ApiResponse.success(list);
    }'''

ctrl_content = ctrl_content.replace(old_ctrl_region, new_ctrl_region)

# 保存修改
with open(CONTROLLER_PATH, 'w', encoding='utf-8') as f:
    f.write(ctrl_content)

print("✅ AnalyticsController.java 已修复")
