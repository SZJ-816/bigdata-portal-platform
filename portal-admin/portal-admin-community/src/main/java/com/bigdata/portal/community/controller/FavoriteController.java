package com.bigdata.portal.community.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.community.entity.UserFavorite;
import com.bigdata.portal.community.service.UserFavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏Controller
 */
@RestController
@RequestMapping("/community/favorite")
public class FavoriteController {

    private final UserFavoriteService userFavoriteService;

    public FavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    /** 查询用户收藏列表 */
    @GetMapping("/user/{userId}")
    @OperationLog(module = "收藏管理", type = "查询", description = "查询用户收藏列表")
    public R<List<UserFavorite>> listByUser(@PathVariable Long userId) {
        return R.ok(userFavoriteService.listByUserId(userId));
    }

    /** 新增收藏 */
    @PostMapping
    @OperationLog(module = "收藏管理", type = "新增", description = "新增收藏")
    public R<Void> add(@RequestBody UserFavorite favorite) {
        userFavoriteService.save(favorite);
        return R.ok();
    }

    /** 取消收藏 */
    @DeleteMapping("/{favoriteId}")
    @OperationLog(module = "收藏管理", type = "删除", description = "取消收藏")
    public R<Void> remove(@PathVariable Long favoriteId) {
        userFavoriteService.deleteById(favoriteId);
        return R.ok();
    }
}
