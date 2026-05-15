package com.bigdata.portal.mapper;

import com.bigdata.portal.entity.UserBehavior;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper {

    @Insert("INSERT INTO user_behavior(user_id, event_type, target_id, target_type, extra, created_at) VALUES(#{userId}, #{eventType}, #{targetId}, #{targetType}, #{extra}, NOW())")
    void insert(UserBehavior behavior);

    @Select("SELECT COUNT(DISTINCT user_id) FROM user_behavior WHERE DATE(created_at) = CURDATE()")
    long countTodayUV();

    @Select("SELECT COUNT(*) FROM user_behavior WHERE DATE(created_at) = CURDATE()")
    long countTodayPV();

    @Select("SELECT COUNT(DISTINCT user_id) FROM user_behavior WHERE created_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE)")
    long countOnlineUsers();

    @Select("SELECT event_type, COUNT(*) as cnt FROM user_behavior WHERE DATE(created_at) = CURDATE() GROUP BY event_type ORDER BY cnt DESC")
    List<Map<String, Object>> countByEventTypeToday();

    @Select("SELECT HOUR(created_at) as hour, COUNT(DISTINCT user_id) as uv, COUNT(*) as pv FROM user_behavior WHERE DATE(created_at) = CURDATE() GROUP BY HOUR(created_at) ORDER BY hour")
    List<Map<String, Object>> trendByHourToday();

    @Select("SELECT COUNT(DISTINCT user_id) FROM user_behavior")
    long countTotalUV();

    @Select("SELECT COUNT(*) FROM user_behavior")
    long countTotalBehaviors();
}
