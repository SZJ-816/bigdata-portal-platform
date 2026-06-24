package com.bigdata.portal.statistics.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.statistics.mapper.ClickHouseStatMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * 数据统计Controller
 * 提供用户行为、PV/UV、热门文章等统计接口
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final ClickHouseStatMapper clickHouseStatMapper;

    public StatisticsController(ClickHouseStatMapper clickHouseStatMapper) {
        this.clickHouseStatMapper = clickHouseStatMapper;
    }

    /** 查询指定日期PV/UV */
    @GetMapping("/pv-uv")
    @OperationLog(module = "数据统计", type = "查询", description = "查询PV/UV")
    public R<Map<String, Object>> getPvUv(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        String dateStr = date != null ? date.toString() : LocalDate.now().toString();
        return R.ok(clickHouseStatMapper.selectPvUvByDate(dateStr));
    }

    /** 查询近7天PV/UV趋势 */
    @GetMapping("/pv-uv/trend")
    @OperationLog(module = "数据统计", type = "查询", description = "查询PV/UV趋势")
    public R<List<Map<String, Object>>> getPvUvTrend() {
        return R.ok(clickHouseStatMapper.selectPvUvTrend7Days());
    }

    /** 查询热门文章TOP10 */
    @GetMapping("/hot-articles")
    @OperationLog(module = "数据统计", type = "查询", description = "查询热门文章")
    public R<List<Map<String, Object>>> getHotArticles() {
        return R.ok(clickHouseStatMapper.selectHotArticlesTop10());
    }

    /** 查询用户行为分布 */
    @GetMapping("/behavior-distribution")
    @OperationLog(module = "数据统计", type = "查询", description = "查询行为分布")
    public R<List<Map<String, Object>>> getBehaviorDistribution() {
        return R.ok(clickHouseStatMapper.selectBehaviorDistribution());
    }

    /** 查询活跃用户TOP10 */
    @GetMapping("/active-users")
    @OperationLog(module = "数据统计", type = "查询", description = "查询活跃用户")
    public R<List<Map<String, Object>>> getActiveUsers() {
        return R.ok(clickHouseStatMapper.selectActiveUsersTop10());
    }

    /** 查询频道访问统计 */
    @GetMapping("/channel")
    @OperationLog(module = "数据统计", type = "查询", description = "查询频道统计")
    public R<List<Map<String, Object>>> getChannelStat() {
        return R.ok(clickHouseStatMapper.selectChannelStat());
    }

    /** 数据总览 - 大屏overview接口 */
    @GetMapping("/overview")
    @OperationLog(module = "数据统计", type = "查询", description = "数据总览")
    public R<Map<String, Object>> getOverview(@RequestParam(required = false, defaultValue = "today") String range) {
        Map<String, Object> result = new HashMap<>();
        String dateStr = LocalDate.now().toString();

        // PV/UV
        Map<String, Object> pvUv = clickHouseStatMapper.selectPvUvByDate(dateStr);
        result.put("todayUsers", pvUv.getOrDefault("uv", 0));
        result.put("totalViews", pvUv.getOrDefault("pv", 0));

        // 行为总数
        List<Map<String, Object>> behaviorDist = clickHouseStatMapper.selectBehaviorDistribution();
        long totalBehaviors = behaviorDist.stream()
                .mapToLong(m -> ((Number) m.getOrDefault("count", 0)).longValue())
                .sum();
        result.put("totalBehaviors", totalBehaviors);

        // 总用户数
        List<Map<String, Object>> activeUsers = clickHouseStatMapper.selectActiveUsersTop10();
        result.put("totalUsers", activeUsers.size() > 0 ? activeUsers.size() : pvUv.getOrDefault("uv", 0));

        // 总新闻数 & 今日新增
        Map<String, Object> newsStats = clickHouseStatMapper.selectNewsStats();
        result.put("totalNews", newsStats.getOrDefault("totalNews", 0));
        result.put("todayNews", newsStats.getOrDefault("todayNews", 0));

        // 阅读记录
        result.put("totalReadHistory", pvUv.getOrDefault("pv", 0));

        return R.ok(result);
    }

    /** 趋势数据 - 大屏trend接口 */
    @GetMapping("/trend")
    @OperationLog(module = "数据统计", type = "查询", description = "趋势数据")
    public R<List<Map<String, Object>>> getTrend(@RequestParam(required = false, defaultValue = "today") String range) {
        List<Map<String, Object>> trendData = clickHouseStatMapper.selectHourlyTrend();
        return R.ok(trendData);
    }

    /** 行为类型分布 - 大屏region-dist/event-dist接口 */
    @GetMapping("/region-dist")
    @OperationLog(module = "数据统计", type = "查询", description = "行为类型分布")
    public R<List<Map<String, Object>>> getRegionDist(@RequestParam(required = false, defaultValue = "today") String range) {
        List<Map<String, Object>> dist = clickHouseStatMapper.selectBehaviorDistribution();
        // 转换为前端期望的格式 {event_type, cnt}
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : dist) {
            Map<String, Object> m = new HashMap<>();
            m.put("event_type", item.get("behaviorType"));
            m.put("cnt", item.get("count"));
            result.add(m);
        }
        return R.ok(result);
    }

    /** 频道分布 - 大屏channel-dist接口 */
    @GetMapping("/channel-dist")
    @OperationLog(module = "数据统计", type = "查询", description = "频道分布")
    public R<List<Map<String, Object>>> getChannelDist(@RequestParam(required = false, defaultValue = "today") String range) {
        return R.ok(clickHouseStatMapper.selectChannelStat());
    }

    /** 热门新闻 - 大屏hot-news接口 */
    @GetMapping("/hot-news")
    @OperationLog(module = "数据统计", type = "查询", description = "热门新闻")
    public R<List<Map<String, Object>>> getHotNews(@RequestParam(required = false, defaultValue = "today") String range) {
        return R.ok(clickHouseStatMapper.selectHotArticlesTop10());
    }

    /** 漏斗数据 - 大屏funnel接口 */
    @GetMapping("/funnel")
    @OperationLog(module = "数据统计", type = "查询", description = "漏斗数据")
    public R<List<Map<String, Object>>> getFunnel(@RequestParam(required = false, defaultValue = "today") String range) {
        List<Map<String, Object>> behaviorDist = clickHouseStatMapper.selectBehaviorDistribution();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : behaviorDist) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", item.get("behaviorType"));
            m.put("value", item.get("count"));
            result.add(m);
        }
        return R.ok(result);
    }

    /** 实时数据 - 大屏realtime接口 */
    @GetMapping("/realtime")
    @OperationLog(module = "数据统计", type = "查询", description = "实时数据")
    public R<Map<String, Object>> getRealtime(@RequestParam(required = false, defaultValue = "today") String range) {
        return getOverview(range);
    }
}
