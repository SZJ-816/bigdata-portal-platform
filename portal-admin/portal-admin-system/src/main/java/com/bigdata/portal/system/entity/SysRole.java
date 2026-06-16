package com.bigdata.portal.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统角色实体
 */
@Data
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色ID */
    private Long roleId;

    /** 角色名称 */
    private String roleName;

    /** 角色标识 */
    private String roleKey;

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
