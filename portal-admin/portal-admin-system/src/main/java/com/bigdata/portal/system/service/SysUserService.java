package com.bigdata.portal.system.service;

import com.bigdata.portal.system.entity.SysUser;

import java.util.List;

/**
 * 系统用户Service接口
 */
public interface SysUserService {

    /** 根据ID查询用户 */
    SysUser getById(Long userId);

    /** 根据用户名查询用户 */
    SysUser getByUsername(String username);

    /** 根据邮箱查询用户 */
    SysUser getByEmail(String email);

    /** 查询用户列表 */
    List<SysUser> list();

    /** 新增用户 */
    boolean save(SysUser user);

    /** 更新用户 */
    boolean update(SysUser user);

    /** 删除用户 */
    boolean deleteById(Long userId);
}
