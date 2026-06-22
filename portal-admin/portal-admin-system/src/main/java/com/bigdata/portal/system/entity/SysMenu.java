package com.bigdata.portal.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统菜单实体
 */
@Data
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    private Long menuId;

    /** 父菜单ID（0为顶级） */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 权限标识 */
    private String permission;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 是否可见（0可见 1隐藏） */
    private Integer visible;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
