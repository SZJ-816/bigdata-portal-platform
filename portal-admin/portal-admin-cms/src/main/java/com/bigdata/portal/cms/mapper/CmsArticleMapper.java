package com.bigdata.portal.cms.mapper;

import com.bigdata.portal.cms.entity.CmsArticle;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * CMS文章Mapper接口
 */
@Mapper
public interface CmsArticleMapper {

    @Select("SELECT * FROM cms_article WHERE article_id = #{articleId}")
    CmsArticle selectById(@Param("articleId") Long articleId);

    @Select("SELECT * FROM cms_article ORDER BY create_time DESC")
    List<CmsArticle> selectList();

    @Select("SELECT * FROM cms_article WHERE channel_id = #{channelId} ORDER BY create_time DESC")
    List<CmsArticle> selectByChannelId(@Param("channelId") Long channelId);

    @Insert("INSERT INTO cms_article(channel_id, title, summary, content, cover_image, source, source_url, " +
            "author, tags, view_count, like_count, comment_count, is_top, status, publish_time, create_by, create_time) " +
            "VALUES(#{channelId}, #{title}, #{summary}, #{content}, #{coverImage}, #{source}, #{sourceUrl}, " +
            "#{author}, #{tags}, #{viewCount}, #{likeCount}, #{commentCount}, #{isTop}, #{status}, " +
            "#{publishTime}, #{createBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "articleId")
    int insert(CmsArticle article);

    @Update("UPDATE cms_article SET channel_id=#{channelId}, title=#{title}, summary=#{summary}, " +
            "content=#{content}, cover_image=#{coverImage}, source=#{source}, source_url=#{sourceUrl}, " +
            "author=#{author}, tags=#{tags}, is_top=#{isTop}, status=#{status}, publish_time=#{publishTime}, " +
            "update_time=NOW() WHERE article_id=#{articleId}")
    int update(CmsArticle article);

    @Delete("DELETE FROM cms_article WHERE article_id = #{articleId}")
    int deleteById(@Param("articleId") Long articleId);
}
