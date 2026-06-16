package com.bigdata.portal.system.service.impl;

import com.bigdata.portal.common.exception.CustomException;
import com.bigdata.portal.system.entity.SysUser;
import com.bigdata.portal.system.mapper.SysUserMapper;
import com.bigdata.portal.system.service.SysUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统用户Service实现
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SysUser getById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public SysUser getByEmail(String email) {
        return sysUserMapper.selectByEmail(email);
    }

    @Override
    public List<SysUser> list() {
        return sysUserMapper.selectList();
    }

    @Override
    public boolean save(SysUser user) {
        // 检查用户名是否已存在
        SysUser existUser = sysUserMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new CustomException(400, "用户名已存在");
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return sysUserMapper.insert(user) > 0;
    }

    @Override
    public boolean update(SysUser user) {
        return sysUserMapper.update(user) > 0;
    }

    @Override
    public boolean deleteById(Long userId) {
        return sysUserMapper.deleteById(userId) > 0;
    }
}
