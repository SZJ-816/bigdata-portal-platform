package com.bigdata.portal.system.service;

import com.bigdata.portal.system.entity.SysRole;

import java.util.List;

/**
 * 系统角色Service接口
 */
public interface SysRoleService {

    SysRole getById(Long roleId);

    List<SysRole> list();

    boolean save(SysRole role);

    boolean update(SysRole role);

    boolean deleteById(Long roleId);
}
