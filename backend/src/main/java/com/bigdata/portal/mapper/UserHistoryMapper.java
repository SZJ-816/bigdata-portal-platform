package com.bigdata.portal.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserHistoryMapper {

    @Insert("INSERT INTO user_history(user_id, news_id, duration, created_at) VALUES(#{userId}, #{newsId}, #{duration}, NOW())")
    void insert(Map<String, Object> history);

    @Select("SELECT h.id, h.news_id as newsId, h.duration, h.created_at as createdAt, n.title, n.image_url as imageUrl FROM user_history h LEFT JOIN news n ON h.news_id = n.id WHERE h.user_id = #{userId} ORDER BY h.created_at DESC")
    List<Map<String, Object>> findByUserId(@Param("userId") Long userId);

    @Select("SELECT IFNULL(AVG(duration), 0) FROM user_history WHERE DATE(created_at) = CURDATE()")
    long avgDurationToday();

    @Select("SELECT COUNT(*) FROM user_history")
    long countTotal();
}
