package com.bigdata.portal.cms.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.cms.entity.CmsArticle;
import com.bigdata.portal.cms.service.CmsArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CMS文章Controller
 */
@RestController
@RequestMapping("/cms/article")
public class CmsArticleController {

    private final CmsArticleService cmsArticleService;

    public CmsArticleController(CmsArticleService cmsArticleService) {
        this.cmsArticleService = cmsArticleService;
    }

    /** 查询文章列表 */
    @GetMapping("/list")
    @OperationLog(module = "文章管理", type = "查询", description = "查询文章列表")
    public R<List<CmsArticle>> list() {
        return R.ok(cmsArticleService.list());
    }

    /** 根据频道查询文章 */
    @GetMapping("/channel/{channelId}")
    @OperationLog(module = "文章管理", type = "查询", description = "根据频道查询文章")
    public R<List<CmsArticle>> listByChannel(@PathVariable Long channelId) {
        return R.ok(cmsArticleService.listByChannelId(channelId));
    }

    /** 根据ID查询文章 */
    @GetMapping("/{articleId}")
    @OperationLog(module = "文章管理", type = "查询", description = "根据ID查询文章")
    public R<CmsArticle> getById(@PathVariable Long articleId) {
        return R.ok(cmsArticleService.getById(articleId));
    }

    /** 新增文章 */
    @PostMapping
    @OperationLog(module = "文章管理", type = "新增", description = "新增文章")
    public R<Void> add(@RequestBody CmsArticle article) {
        cmsArticleService.save(article);
        return R.ok();
    }

    /** 更新文章 */
    @PutMapping
    @OperationLog(module = "文章管理", type = "修改", description = "更新文章")
    public R<Void> edit(@RequestBody CmsArticle article) {
        cmsArticleService.update(article);
        return R.ok();
    }

    /** 删除文章 */
    @DeleteMapping("/{articleId}")
    @OperationLog(module = "文章管理", type = "删除", description = "删除文章")
    public R<Void> remove(@PathVariable Long articleId) {
        cmsArticleService.deleteById(articleId);
        return R.ok();
    }
}
