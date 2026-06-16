package com.bigdata.portal.system.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.system.entity.SysRole;
import com.bigdata.portal.system.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统角色Controller
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /** 查询角色列表 */
    @GetMapping("/list")
    @OperationLog(module = "角色管理", type = "查询", description = "查询角色列表")
    public R<List<SysRole>> list() {
        return R.ok(sysRoleService.list());
    }

    /** 根据ID查询角色 */
    @GetMapping("/{roleId}")
    @OperationLog(module = "角色管理", type = "查询", description = "根据ID查询角色")
    public R<SysRole> getById(@PathVariable Long roleId) {
        return R.ok(sysRoleService.getById(roleId));
    }

    /** 新增角色 */
    @PostMapping
    @OperationLog(module = "角色管理", type = "新增", description = "新增角色")
    public R<Void> add(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return R.ok();
    }

    /** 更新角色 */
    @PutMapping
    @OperationLog(module = "角色管理", type = "修改", description = "更新角色")
    public R<Void> edit(@RequestBody SysRole role) {
        sysRoleService.update(role);
        return R.ok();
    }

    /** 删除角色 */
    @DeleteMapping("/{roleId}")
    @OperationLog(module = "角色管理", type = "删除", description = "删除角色")
    public R<Void> remove(@PathVariable Long roleId) {
        sysRoleService.deleteById(roleId);
        return R.ok();
    }
}
