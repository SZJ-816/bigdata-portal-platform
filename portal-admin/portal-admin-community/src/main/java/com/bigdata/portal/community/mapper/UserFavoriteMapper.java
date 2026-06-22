package com.bigdata.portal.community.mapper;

import com.bigdata.portal.community.entity.UserFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户收藏Mapper接口
 */
@Mapper
public interface UserFavoriteMapper {

    @Select("SELECT * FROM user_favorite WHERE favorite_id = #{favoriteId}")
    UserFavorite selectById(@Param("favoriteId") Long favoriteId);

    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserFavorite> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND article_id = #{articleId}")
    UserFavorite selectByUserAndArticle(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Insert("INSERT INTO user_favorite(user_id, article_id, create_time) VALUES(#{userId}, #{articleId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "favoriteId")
    int insert(UserFavorite favorite);

    @Delete("DELETE FROM user_favorite WHERE favorite_id = #{favoriteId}")
    int deleteById(@Param("favoriteId") Long favoriteId);
}
