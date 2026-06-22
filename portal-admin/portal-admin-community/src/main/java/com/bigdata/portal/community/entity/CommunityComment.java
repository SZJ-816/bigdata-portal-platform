package com.bigdata.portal.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 社区评论实体
 */
@Data
public class CommunityComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 文章ID */
    private Long articleId;

    /** 用户ID */
    private Long userId;

    /** 父评论ID（0为顶级评论） */
    private Long parentId;

    /** 评论内容 */
    private String content;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态（0正常 1隐藏） */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
