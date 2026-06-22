package com.bigdata.portal.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统配置实体
 */
@Data
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 配置名称 */
    private String configName;

    /** 配置键 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 系统内置（Y是 N否） */
    private String isSystem;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    private String remark;
}
