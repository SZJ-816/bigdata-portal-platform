package com.bigdata.portal.mapper;

import com.bigdata.portal.entity.UserBehavior;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserBehaviorMapper {

    @Insert("INSERT INTO user_behavior(user_id, event_type, target_id, target_type, extra, created_at) VALUES(#{userId}, #{eventType}, #{targetId}, #{targetType}, #{extra}, NOW())")
    void insert(UserBehavior behavior);
}
