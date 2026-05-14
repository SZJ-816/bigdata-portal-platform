package com.bigdata.portal.mapper;

import com.bigdata.portal.entity.Comment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("SELECT * FROM comment WHERE news_id = #{newsId} ORDER BY created_at DESC")
    List<Comment> findByNewsId(@Param("newsId") Long newsId);

    @Insert("INSERT INTO comment(user_id, news_id, content, parent_id, like_count, created_at) VALUES(#{userId}, #{newsId}, #{content}, #{parentId}, #{likeCount}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comment comment);

    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLike(@Param("id") Long id);
}
