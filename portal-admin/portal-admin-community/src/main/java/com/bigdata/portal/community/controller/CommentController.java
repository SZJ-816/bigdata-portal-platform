package com.bigdata.portal.community.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.community.entity.CommunityComment;
import com.bigdata.portal.community.service.CommunityCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论Controller
 */
@RestController
@RequestMapping("/community/comment")
public class CommentController {

    private final CommunityCommentService communityCommentService;

    public CommentController(CommunityCommentService communityCommentService) {
        this.communityCommentService = communityCommentService;
    }

    /** 查询评论列表 */
    @GetMapping("/list")
    @OperationLog(module = "评论管理", type = "查询", description = "查询评论列表")
    public R<List<CommunityComment>> list() {
        return R.ok(communityCommentService.list());
    }

    /** 根据文章查询评论 */
    @GetMapping("/article/{articleId}")
    @OperationLog(module = "评论管理", type = "查询", description = "根据文章查询评论")
    public R<List<CommunityComment>> listByArticle(@PathVariable Long articleId) {
        return R.ok(communityCommentService.listByArticleId(articleId));
    }

    /** 新增评论 */
    @PostMapping
    @OperationLog(module = "评论管理", type = "新增", description = "新增评论")
    public R<Void> add(@RequestBody CommunityComment comment) {
        communityCommentService.save(comment);
        return R.ok();
    }

    /** 更新评论状态 */
    @PutMapping
    @OperationLog(module = "评论管理", type = "修改", description = "更新评论")
    public R<Void> edit(@RequestBody CommunityComment comment) {
        communityCommentService.update(comment);
        return R.ok();
    }

    /** 删除评论 */
    @DeleteMapping("/{commentId}")
    @OperationLog(module = "评论管理", type = "删除", description = "删除评论")
    public R<Void> remove(@PathVariable Long commentId) {
        communityCommentService.deleteById(commentId);
        return R.ok();
    }
}
