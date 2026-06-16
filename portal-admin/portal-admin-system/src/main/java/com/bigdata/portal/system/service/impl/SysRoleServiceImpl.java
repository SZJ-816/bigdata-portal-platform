package com.bigdata.portal.system.service.impl;

import com.bigdata.portal.system.entity.SysRole;
import com.bigdata.portal.system.mapper.SysRoleMapper;
import com.bigdata.portal.system.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统角色Service实现
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public SysRole getById(Long roleId) {
        return sysRoleMapper.selectById(roleId);
    }

    @Override
    public List<SysRole> list() {
        return sysRoleMapper.selectList();
    }

    @Override
    public boolean save(SysRole role) {
        return sysRoleMapper.insert(role) > 0;
    }

    @Override
    public boolean update(SysRole role) {
        return sysRoleMapper.update(role) > 0;
    }

    @Override
    public boolean deleteById(Long roleId) {
        return sysRoleMapper.deleteById(roleId) > 0;
    }
}
