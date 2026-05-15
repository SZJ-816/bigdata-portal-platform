package com.bigdata.portal.mapper;

import com.bigdata.portal.entity.News;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface NewsMapper {

    @Select("SELECT id, title, summary, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, created_at FROM news ORDER BY publish_time DESC LIMIT #{offset}, #{limit}")
    List<News> findPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM news")
    long count();

    @Select("SELECT id, title, summary, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, created_at FROM news WHERE channel = #{channel} ORDER BY publish_time DESC LIMIT #{offset}, #{limit}")
    List<News> findByChannelPage(@Param("channel") String channel, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM news WHERE channel = #{channel}")
    long countByChannel(@Param("channel") String channel);

    @Select("SELECT * FROM news ORDER BY publish_time DESC")
    List<News> findAll();

    @Select("SELECT id, title, summary, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, created_at FROM news WHERE title LIKE CONCAT('%',#{keyword},'%') OR summary LIKE CONCAT('%',#{keyword},'%') OR channel LIKE CONCAT('%',#{keyword},'%') ORDER BY publish_time DESC LIMIT #{offset}, #{limit}")
    List<News> searchPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT id, title, summary, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, created_at FROM news WHERE title LIKE CONCAT('%',#{keyword},'%') OR summary LIKE CONCAT('%',#{keyword},'%') OR channel LIKE CONCAT('%',#{keyword},'%') ORDER BY publish_time DESC")
    List<News> search(@Param("keyword") String keyword);

    @Select("SELECT id, title, summary, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, created_at FROM news ORDER BY publish_time DESC LIMIT 20")
    List<News> findTop20();

    @Select("SELECT id, title, summary, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, created_at FROM news WHERE channel = #{channel} ORDER BY publish_time DESC LIMIT 20")
    List<News> findByChannelTop20(@Param("channel") String channel);

    @Select("SELECT COUNT(*) FROM news WHERE title LIKE CONCAT('%',#{keyword},'%') OR summary LIKE CONCAT('%',#{keyword},'%') OR channel LIKE CONCAT('%',#{keyword},'%')")
    long countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM news WHERE id = #{id}")
    News findById(@Param("id") Long id);

    @Select("SELECT * FROM news WHERE channel = #{channel} ORDER BY publish_time DESC")
    List<News> findByChannel(@Param("channel") String channel);

    @Select("SELECT * FROM news WHERE is_breaking = 1 ORDER BY publish_time DESC")
    List<News> findBreaking();

    @Select("SELECT * FROM news WHERE source_url = #{sourceUrl} LIMIT 1")
    News findBySourceUrl(@Param("sourceUrl") String sourceUrl);

    @Update("UPDATE news SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementView(@Param("id") Long id);

    @Update("UPDATE news SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementComment(@Param("id") Long id);

    @Insert("INSERT INTO news(title, summary, content, channel, source, image_url, view_count, comment_count, is_breaking, publish_time, created_at, updated_at, source_url) VALUES(#{title}, #{summary}, #{content}, #{channel}, #{source}, #{imageUrl}, #{viewCount}, #{commentCount}, #{isBreaking}, #{publishTime}, NOW(), NOW(), #{sourceUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(News news);

    @Update("UPDATE news SET summary = #{summary}, image_url = #{imageUrl}, updated_at = NOW() WHERE id = #{id}")
    void update(News news);
}
