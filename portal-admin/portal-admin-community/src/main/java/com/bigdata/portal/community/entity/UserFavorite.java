package com.bigdata.portal.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户收藏实体
 */
@Data
public class UserFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 收藏ID */
    private Long favoriteId;

    /** 用户ID */
    private Long userId;

    /** 文章ID */
    private Long articleId;

    /** 创建时间 */
    private Date createTime;
}
