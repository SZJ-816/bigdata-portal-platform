package com.bigdata.portal.cms.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.cms.entity.CmsArticle;
import com.bigdata.portal.cms.entity.CmsChannel;
import com.bigdata.portal.cms.service.CmsArticleService;
import com.bigdata.portal.cms.service.CmsChannelService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CMS文章Controller
 */
@RestController
@RequestMapping("/cms/article")
public class CmsArticleController {

    private final CmsArticleService cmsArticleService;
    private final CmsChannelService cmsChannelService;
    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();

    public CmsArticleController(CmsArticleService cmsArticleService, CmsChannelService cmsChannelService) {
        this.cmsArticleService = cmsArticleService;
        this.cmsChannelService = cmsChannelService;
    }

    /** SSE流式响应接口 */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseExecutor.execute(() -> {
            try {
                emitter.send(SseEmitter.event().name("message").data("{\"type\":\"complete\",\"message\":\"stream ready\"}"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    /** 查询文章列表 */
    @GetMapping("/list")
    @OperationLog(module = "文章管理", type = "查询", description = "查询文章列表")
    public R<List<CmsArticle>> list() {
        return R.ok(cmsArticleService.list());
    }

    /** 按频道分组查询新闻 */
    @GetMapping("/channels")
    @OperationLog(module = "文章管理", type = "查询", description = "按频道分组查询新闻")
    public R<Map<String, List<CmsArticle>>> listByChannels(@RequestParam(defaultValue = "4") int size) {
        List<CmsChannel> channels = cmsChannelService.list();
        Map<String, List<CmsArticle>> result = new LinkedHashMap<>();
        for (CmsChannel channel : channels) {
            List<CmsArticle> articles = cmsArticleService.listByChannelId(channel.getChannelId());
            if (articles.size() > size) {
                articles = articles.subList(0, size);
            }
            result.put(channel.getChannelKey(), articles);
        }
        return R.ok(result);
    }

    /** 根据频道查询文章 */
    @GetMapping("/channel/{channelId}")
    @OperationLog(module = "文章管理", type = "查询", description = "根据频道查询文章")
    public R<List<CmsArticle>> listByChannel(@PathVariable Long channelId) {
        return R.ok(cmsArticleService.listByChannelId(channelId));
    }

    /** 根据频道key查询文章 */
    @GetMapping("/channel-key/{channelKey}")
    @OperationLog(module = "文章管理", type = "查询", description = "根据频道key查询文章")
    public R<List<CmsArticle>> listByChannelKey(@PathVariable String channelKey) {
        return R.ok(cmsArticleService.listByChannelKey(channelKey));
    }

    /** 根据ID查询文章 */
    @GetMapping("/detail/{articleId}")
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
