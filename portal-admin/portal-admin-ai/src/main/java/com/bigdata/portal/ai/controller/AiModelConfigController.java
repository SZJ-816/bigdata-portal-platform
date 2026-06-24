package com.bigdata.portal.ai.controller;

import com.bigdata.portal.ai.entity.AiModelConfig;
import com.bigdata.portal.ai.mapper.AiModelConfigMapper;
import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI模型配置Controller
 * 提供AI模型配置的增删改查接口
 */
@RestController
@RequestMapping("/ai/config")
public class AiModelConfigController {

    private final AiModelConfigMapper configMapper;

    public AiModelConfigController(AiModelConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    /**
     * 获取所有模型配置
     */
    @GetMapping("/list")
    @OperationLog(module = "AI配置", type = "查询", description = "获取AI模型配置列表")
    public R<List<AiModelConfig>> list() {
        return R.ok(configMapper.findAll());
    }

    /**
     * 获取默认模型配置
     */
    @GetMapping("/default")
    public R<AiModelConfig> getDefault() {
        return R.ok(configMapper.findDefault());
    }

    /**
     * 根据ID获取配置
     */
    @GetMapping("/{id}")
    public R<AiModelConfig> getById(@PathVariable Long id) {
        return R.ok(configMapper.findById(id));
    }

    /**
     * 新增模型配置
     */
    @PostMapping
    @OperationLog(module = "AI配置", type = "新增", description = "新增AI模型配置")
    public R<Void> add(@RequestBody AiModelConfig config) {
        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            configMapper.clearDefault();
        }
        configMapper.insert(config);
        return R.ok();
    }

    /**
     * 更新模型配置
     */
    @PutMapping("/{id}")
    @OperationLog(module = "AI配置", type = "更新", description = "更新AI模型配置")
    public R<Void> update(@PathVariable Long id, @RequestBody AiModelConfig config) {
        config.setId(id);
        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            configMapper.clearDefault();
        }
        configMapper.update(config);
        return R.ok();
    }

    /**
     * 删除模型配置
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "AI配置", type = "删除", description = "删除AI模型配置")
    public R<Void> delete(@PathVariable Long id) {
        configMapper.deleteById(id);
        return R.ok();
    }

    /**
     * 设置默认模型
     */
    @PutMapping("/{id}/default")
    @OperationLog(module = "AI配置", type = "设置默认", description = "设置默认AI模型")
    public R<Void> setDefault(@PathVariable Long id) {
        configMapper.clearDefault();
        configMapper.setDefault(id);
        return R.ok();
    }
}
