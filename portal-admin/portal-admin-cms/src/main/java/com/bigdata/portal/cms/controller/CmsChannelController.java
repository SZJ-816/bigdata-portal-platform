package com.bigdata.portal.cms.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.cms.entity.CmsChannel;
import com.bigdata.portal.cms.service.CmsChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CMS频道Controller
 */
@RestController
@RequestMapping("/cms/channel")
public class CmsChannelController {

    private final CmsChannelService cmsChannelService;

    public CmsChannelController(CmsChannelService cmsChannelService) {
        this.cmsChannelService = cmsChannelService;
    }

    /** 查询频道列表 */
    @GetMapping("/list")
    @OperationLog(module = "频道管理", type = "查询", description = "查询频道列表")
    public R<List<CmsChannel>> list() {
        return R.ok(cmsChannelService.list());
    }

    /** 根据ID查询频道 */
    @GetMapping("/{channelId}")
    @OperationLog(module = "频道管理", type = "查询", description = "根据ID查询频道")
    public R<CmsChannel> getById(@PathVariable Long channelId) {
        return R.ok(cmsChannelService.getById(channelId));
    }

    /** 新增频道 */
    @PostMapping
    @OperationLog(module = "频道管理", type = "新增", description = "新增频道")
    public R<Void> add(@RequestBody CmsChannel channel) {
        cmsChannelService.save(channel);
        return R.ok();
    }

    /** 更新频道 */
    @PutMapping
    @OperationLog(module = "频道管理", type = "修改", description = "更新频道")
    public R<Void> edit(@RequestBody CmsChannel channel) {
        cmsChannelService.update(channel);
        return R.ok();
    }

    /** 删除频道 */
    @DeleteMapping("/{channelId}")
    @OperationLog(module = "频道管理", type = "删除", description = "删除频道")
    public R<Void> remove(@PathVariable Long channelId) {
        cmsChannelService.deleteById(channelId);
        return R.ok();
    }
}
