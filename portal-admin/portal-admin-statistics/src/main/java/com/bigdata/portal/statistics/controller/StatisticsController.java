package com.bigdata.portal.statistics.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.statistics.mapper.ClickHouseStatMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
}
