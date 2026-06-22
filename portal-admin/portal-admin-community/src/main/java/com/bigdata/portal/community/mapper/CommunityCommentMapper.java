package com.bigdata.portal.community.mapper;

import com.bigdata.portal.community.entity.CommunityComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 社区评论Mapper接口
 */
@Mapper
public interface CommunityCommentMapper {

    @Select("SELECT * FROM community_comment WHERE comment_id = #{commentId}")
    CommunityComment selectById(@Param("commentId") Long commentId);

    @Select("SELECT * FROM community_comment WHERE article_id = #{articleId} ORDER BY create_time DESC")
    List<CommunityComment> selectByArticleId(@Param("articleId") Long articleId);

    @Select("SELECT * FROM community_comment ORDER BY create_time DESC")
    List<CommunityComment> selectList();

    @Insert("INSERT INTO community_comment(article_id, user_id, parent_id, content, like_count, status, create_time) " +
            "VALUES(#{articleId}, #{userId}, #{parentId}, #{content}, #{likeCount}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insert(CommunityComment comment);

    @Update("UPDATE community_comment SET content=#{content}, status=#{status}, update_time=NOW() " +
            "WHERE comment_id=#{commentId}")
    int update(CommunityComment comment);

    @Delete("DELETE FROM community_comment WHERE comment_id = #{commentId}")
    int deleteById(@Param("commentId") Long commentId);
}
