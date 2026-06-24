package com.bigdata.portal.cms.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * CMS文章实体
 */
@Data
public class CmsArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章ID */
    private Long articleId;

    /** 频道ID */
    private Long channelId;

    /** 文章标题 */
    private String title;

    /** 文章摘要 */
    private String summary;

    /** 文章内容 */
    private String content;

    /** 封面图 */
    private String coverImage;

    /** 来源 */
    private String source;

    /** 原文链接 */
    private String sourceUrl;

    /** 作者 */
    private String author;

    /** 标签（逗号分隔） */
    private String tags;

    /** 浏览量 */
    private Long viewCount;

    /** 点赞数 */
    private Long likeCount;

    /** 评论数 */
    private Long commentCount;

    /** 是否置顶（0否 1是） */
    private Integer isTop;

    /** 发布状态（0草稿 1已发布 2已下架） */
    private Integer status;

    /** 发布时间 */
    private Date publishTime;

    /** 创建人ID */
    private Long createBy;

    /** 创建时间 */
    private Date createTime;

    /** 频道标识（来自cms_channel.channel_key） */
    private String channel;

    /** 频道名称（来自cms_channel.channel_name） */
    private String channelName;

    /** 更新时间 */
    private Date updateTime;
}
