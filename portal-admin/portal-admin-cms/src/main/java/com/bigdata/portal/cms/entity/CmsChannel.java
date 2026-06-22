package com.bigdata.portal.cms.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * CMS频道实体
 */
@Data
public class CmsChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 频道ID */
    private Long channelId;

    /** 父频道ID（0为顶级） */
    private Long parentId;

    /** 频道名称 */
    private String channelName;

    /** 频道标识 */
    private String channelKey;

    /** 频道图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    private String remark;
}
