package com.bigdata.portal.cms.mapper;

import com.bigdata.portal.cms.entity.CmsChannel;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * CMS频道Mapper接口
 */
@Mapper
public interface CmsChannelMapper {

    @Select("SELECT * FROM cms_channel WHERE channel_id = #{channelId}")
    CmsChannel selectById(@Param("channelId") Long channelId);

    @Select("SELECT * FROM cms_channel ORDER BY sort")
    List<CmsChannel> selectList();

    @Insert("INSERT INTO cms_channel(parent_id, channel_name, channel_key, icon, sort, status, create_time, remark) " +
            "VALUES(#{parentId}, #{channelName}, #{channelKey}, #{icon}, #{sort}, #{status}, NOW(), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "channelId")
    int insert(CmsChannel channel);

    @Update("UPDATE cms_channel SET parent_id=#{parentId}, channel_name=#{channelName}, " +
            "channel_key=#{channelKey}, icon=#{icon}, sort=#{sort}, status=#{status}, " +
            "update_time=NOW(), remark=#{remark} WHERE channel_id=#{channelId}")
    int update(CmsChannel channel);

    @Delete("DELETE FROM cms_channel WHERE channel_id = #{channelId}")
    int deleteById(@Param("channelId") Long channelId);
}
