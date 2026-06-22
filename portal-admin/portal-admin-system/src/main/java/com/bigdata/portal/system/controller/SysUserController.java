package com.bigdata.portal.system.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.system.entity.SysUser;
import com.bigdata.portal.system.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户Controller
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /** 查询用户列表 */
    @GetMapping("/list")
    @OperationLog(module = "用户管理", type = "查询", description = "查询用户列表")
    public R<List<SysUser>> list() {
        return R.ok(sysUserService.list());
    }

    /** 根据ID查询用户 */
    @GetMapping("/{userId}")
    @OperationLog(module = "用户管理", type = "查询", description = "根据ID查询用户")
    public R<SysUser> getById(@PathVariable Long userId) {
        return R.ok(sysUserService.getById(userId));
    }

    /** 新增用户 */
    @PostMapping
    @OperationLog(module = "用户管理", type = "新增", description = "新增用户")
    public R<Void> add(@RequestBody SysUser user) {
        sysUserService.save(user);
        return R.ok();
    }

    /** 更新用户 */
    @PutMapping
    @OperationLog(module = "用户管理", type = "修改", description = "更新用户")
    public R<Void> edit(@RequestBody SysUser user) {
        sysUserService.update(user);
        return R.ok();
    }

    /** 删除用户 */
    @DeleteMapping("/{userId}")
    @OperationLog(module = "用户管理", type = "删除", description = "删除用户")
    public R<Void> remove(@PathVariable Long userId) {
        sysUserService.deleteById(userId);
        return R.ok();
    }
}
