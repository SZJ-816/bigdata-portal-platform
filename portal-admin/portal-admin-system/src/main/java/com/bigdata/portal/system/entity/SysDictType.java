package com.bigdata.portal.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典类型实体
 */
@Data
public class SysDictType implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 字典类型ID */
    private Long dictTypeId;

    /** 字典类型名称 */
    private String dictName;

    /** 字典类型标识 */
    private String dictType;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    private String remark;
}
