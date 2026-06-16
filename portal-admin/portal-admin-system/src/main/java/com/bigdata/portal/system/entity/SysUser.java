package com.bigdata.portal.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户实体
 */
@Data
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 密码（加密存储） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 性别（0未知 1男 2女） */
    private Integer gender;

    /** 头像地址 */
    private String avatar;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    private String remark;
}
