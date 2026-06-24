package com.bigdata.portal.community.controller;

import com.bigdata.portal.common.result.R;
import com.bigdata.portal.community.mapper.UserBehaviorMapper;
import com.bigdata.portal.community.entity.UserBehavior;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户行为Controller
 */
@RestController
@RequestMapping("/community/behavior")
public class BehaviorController {

    private final UserBehaviorMapper userBehaviorMapper;

    public BehaviorController(UserBehaviorMapper userBehaviorMapper) {
        this.userBehaviorMapper = userBehaviorMapper;
    }

    @GetMapping
    public R<List<UserBehavior>> list(@RequestParam(required = false) Long userId,
                                       @RequestParam(required = false) Long articleId) {
        if (userId != null) {
            return R.ok(userBehaviorMapper.selectByUserId(userId));
        }
        if (articleId != null) {
            return R.ok(userBehaviorMapper.selectByArticleId(articleId));
        }
        // 默认返回最近行为
        return R.ok(userBehaviorMapper.selectRecent(50));
    }
}
