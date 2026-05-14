package com.bigdata.portal.mapper;

import com.bigdata.portal.entity.UserFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {

    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserFavorite> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND news_id = #{newsId}")
    UserFavorite findByUserIdAndNewsId(@Param("userId") Long userId, @Param("newsId") Long newsId);

    @Insert("INSERT INTO user_favorite(user_id, news_id, created_at) VALUES(#{userId}, #{newsId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserFavorite favorite);

    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND news_id = #{newsId}")
    void delete(@Param("userId") Long userId, @Param("newsId") Long newsId);
}
