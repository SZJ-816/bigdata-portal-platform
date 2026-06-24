package com.bigdata.portal.statistics.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 统计查询Mapper
 * 查询用户行为、PV/UV、热门文章等统计数据
 * 当前使用MySQL的user_behavior表查询
 */
@Mapper
public interface ClickHouseStatMapper {

    /**
     * 查询今日PV/UV
     */
    @Select("SELECT " +
            "COUNT(*) AS pv, " +
            "COUNT(DISTINCT user_id) AS uv " +
            "FROM user_behavior " +
            "WHERE DATE(create_time) = #{date}")
    Map<String, Object> selectPvUvByDate(@Param("date") String date);

    /**
     * 查询近7天每日PV/UV趋势
     */
    @Select("SELECT " +
            "DATE(create_time) AS date, " +
            "COUNT(*) AS pv, " +
            "COUNT(DISTINCT user_id) AS uv " +
            "FROM user_behavior " +
            "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY date")
    List<Map<String, Object>> selectPvUvTrend7Days();

    /**
     * 查询热门文章TOP10（按浏览量）
     */
    @Select("SELECT " +
            "article_id AS articleId, " +
            "COUNT(*) AS viewCount, " +
            "COUNT(DISTINCT user_id) AS uniqueVisitor " +
            "FROM user_behavior " +
            "WHERE behavior_type = 'VIEW' " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY article_id " +
            "ORDER BY viewCount DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectHotArticlesTop10();

    /**
     * 查询用户行为分布
     */
    @Select("SELECT " +
            "behavior_type AS behaviorType, " +
            "COUNT(*) AS count " +
            "FROM user_behavior " +
            "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY behavior_type " +
            "ORDER BY count DESC")
    List<Map<String, Object>> selectBehaviorDistribution();

    /**
     * 查询活跃用户TOP10
     */
    @Select("SELECT " +
            "user_id AS userId, " +
            "COUNT(*) AS actionCount " +
            "FROM user_behavior " +
            "WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY user_id " +
            "ORDER BY actionCount DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectActiveUsersTop10();

    /**
     * 查询频道访问统计
     */
    @Select("SELECT " +
            "c.channel_id AS channelId, " +
            "c.channel_name AS channelName, " +
            "COUNT(*) AS viewCount " +
            "FROM user_behavior ub " +
            "JOIN cms_article a ON ub.article_id = a.article_id " +
            "JOIN cms_channel c ON a.channel_id = c.channel_id " +
            "WHERE ub.behavior_type = 'VIEW' " +
            "AND ub.create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY c.channel_id, c.channel_name " +
            "ORDER BY viewCount DESC")
    List<Map<String, Object>> selectChannelStat();

    /**
     * 查询新闻统计（总数和今日新增）
     */
    @Select("SELECT " +
            "COUNT(*) AS totalNews, " +
            "SUM(CASE WHEN DATE(create_time) = CURDATE() THEN 1 ELSE 0 END) AS todayNews " +
            "FROM cms_article")
    Map<String, Object> selectNewsStats();

    /**
     * 查询今日按小时PV/UV趋势（大屏trend接口）
     */
    @Select("SELECT " +
            "HOUR(create_time) AS hour, " +
            "COUNT(*) AS pv, " +
            "COUNT(DISTINCT user_id) AS uv " +
            "FROM user_behavior " +
            "WHERE DATE(create_time) = CURDATE() " +
            "GROUP BY HOUR(create_time) " +
            "ORDER BY hour")
    List<Map<String, Object>> selectHourlyTrend();
}
