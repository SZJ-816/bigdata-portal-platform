package com.bigdata.portal.controller;

import com.bigdata.portal.config.JwtConfig;
import com.bigdata.portal.entity.User;
import com.bigdata.portal.entity.UserBehavior;
import com.bigdata.portal.entity.UserFavorite;
import com.bigdata.portal.kafka.BehaviorProducer;
import com.bigdata.portal.mapper.UserBehaviorMapper;
import com.bigdata.portal.mapper.UserFavoriteMapper;
import com.bigdata.portal.mapper.UserHistoryMapper;
import com.bigdata.portal.mapper.UserMapper;
import com.bigdata.portal.service.EmailService;
import com.bigdata.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    @Autowired
    private UserFavoriteMapper favoriteMapper;

    @Autowired
    private UserHistoryMapper historyMapper;

    @Autowired
    private BehaviorProducer behaviorProducer;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = userService.login(params.get("username"), params.get("password"));
        if (data != null) {
            result.put("data", data);
            result.put("success", true);
        } else {
            result.put("error", "用户名或密码错误");
            result.put("success", false);
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String email = params.get("email");
        String code = params.get("code");

        if (email == null || email.trim().isEmpty()) {
            result.put("error", "请填写邮箱");
            result.put("success", false);
            return result;
        }
        if (code == null || code.trim().isEmpty()) {
            result.put("error", "请填写邮箱验证码");
            result.put("success", false);
            return result;
        }
        if (!emailService.verifyCode(email, code)) {
            result.put("error", "验证码错误或已过期");
            result.put("success", false);
            return result;
        }

        Map<String, Object> data = userService.register(
            params.get("username"), params.get("password"), email);
        if (data != null) {
            result.put("data", data);
            result.put("success", true);
        } else {
            result.put("error", "用户名已存在");
            result.put("success", false);
        }
        return result;
    }

    @PostMapping("/send-code")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String email = params.get("email");

        if (email == null || email.trim().isEmpty()) {
            result.put("error", "请填写邮箱");
            result.put("success", false);
            return result;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            result.put("error", "邮箱格式不正确");
            result.put("success", false);
            return result;
        }

        User existing = userMapper.findByEmail(email);
        if (existing != null) {
            result.put("error", "该邮箱已被注册");
            result.put("success", false);
            return result;
        }

        boolean sent = emailService.sendVerificationCode(email);
        if (sent) {
            result.put("message", "验证码已发送");
            result.put("success", true);
        } else {
            result.put("error", "验证码发送失败，请稍后重试");
            result.put("success", false);
        }
        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        String token = jwtConfig.extractToken(authHeader);
        if (token == null || !jwtConfig.validateToken(token)) {
            result.put("error", "未登录或token无效");
            result.put("success", false);
            return result;
        }
        Long userId = jwtConfig.getUserIdFromToken(token);
        User user = userService.getProfile(userId);
        if (user != null) {
            result.put("data", user);
            result.put("success", true);
        } else {
            result.put("error", "用户不存在");
            result.put("success", false);
        }
        return result;
    }

    @GetMapping("/favorites")
    public Map<String, Object> getFavorites(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        String token = jwtConfig.extractToken(authHeader);
        if (token == null || !jwtConfig.validateToken(token)) {
            result.put("error", "未登录或token无效");
            result.put("success", false);
            return result;
        }
        Long userId = jwtConfig.getUserIdFromToken(token);
        List<UserFavorite> favorites = favoriteMapper.findByUserId(userId);
        result.put("data", favorites);
        result.put("success", true);
        return result;
    }

    @PostMapping("/favorites/{newsId}")
    public Map<String, Object> addFavorite(
            @PathVariable Long newsId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        String token = jwtConfig.extractToken(authHeader);
        if (token == null || !jwtConfig.validateToken(token)) {
            result.put("error", "未登录或token无效");
            result.put("success", false);
            return result;
        }
        Long userId = jwtConfig.getUserIdFromToken(token);
        UserFavorite existing = favoriteMapper.findByUserIdAndNewsId(userId, newsId);
        if (existing != null) {
            result.put("message", "已经收藏过");
            result.put("success", true);
            return result;
        }
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setNewsId(newsId);
        favoriteMapper.insert(favorite);
        result.put("success", true);
        return result;
    }

    @DeleteMapping("/favorites/{newsId}")
    public Map<String, Object> removeFavorite(
            @PathVariable Long newsId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        String token = jwtConfig.extractToken(authHeader);
        if (token == null || !jwtConfig.validateToken(token)) {
            result.put("error", "未登录或token无效");
            result.put("success", false);
            return result;
        }
        Long userId = jwtConfig.getUserIdFromToken(token);
        favoriteMapper.delete(userId, newsId);
        result.put("success", true);
        return result;
    }

    @GetMapping("/history")
    public Map<String, Object> getHistory(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        String token = jwtConfig.extractToken(authHeader);
        if (token == null || !jwtConfig.validateToken(token)) {
            result.put("error", "未登录或token无效");
            result.put("success", false);
            return result;
        }
        Long userId = jwtConfig.getUserIdFromToken(token);
        List<Map<String, Object>> history = historyMapper.findByUserId(userId);
        result.put("data", history);
        result.put("success", true);
        return result;
    }

    @PostMapping("/history")
    public Map<String, Object> addHistory(
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        String token = jwtConfig.extractToken(authHeader);
        if (token == null || !jwtConfig.validateToken(token)) {
            result.put("error", "未登录或token无效");
            result.put("success", false);
            return result;
        }
        Long userId = jwtConfig.getUserIdFromToken(token);
        Map<String, Object> history = new HashMap<>();
        history.put("userId", userId);
        history.put("newsId", params.get("newsId"));
        history.put("duration", params.getOrDefault("duration", 0));
        historyMapper.insert(history);
        result.put("success", true);
        return result;
    }
}
