package com.bigdata.portal.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为实体
 * 记录用户浏览、点赞、分享等行为数据
 */
@Data
public class UserBehavior implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 行为ID */
    private Long behaviorId;

    /** 用户ID */
    private Long userId;

    /** 文章ID */
    private Long articleId;

    /** 行为类型（VIEW浏览 LIKE点赞 SHARE分享 FAVORITE收藏） */
    private String behaviorType;

    /** 停留时长（秒） */
    private Integer duration;

    /** 创建时间 */
    private Date createTime;
}
