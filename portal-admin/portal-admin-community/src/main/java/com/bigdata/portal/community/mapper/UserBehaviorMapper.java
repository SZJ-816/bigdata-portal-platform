package com.bigdata.portal.community.mapper;

import com.bigdata.portal.community.entity.UserBehavior;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户行为Mapper接口
 */
@Mapper
public interface UserBehaviorMapper {

    @Select("SELECT * FROM user_behavior WHERE behavior_id = #{behaviorId}")
    UserBehavior selectById(@Param("behaviorId") Long behaviorId);

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserBehavior> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_behavior WHERE article_id = #{articleId} ORDER BY create_time DESC")
    List<UserBehavior> selectByArticleId(@Param("articleId") Long articleId);

    @Insert("INSERT INTO user_behavior(user_id, article_id, behavior_type, duration, create_time) " +
            "VALUES(#{userId}, #{articleId}, #{behaviorType}, #{duration}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "behaviorId")
    int insert(UserBehavior behavior);
}
