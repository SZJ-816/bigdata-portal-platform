package com.bigdata.portal.system.controller;

import com.bigdata.portal.common.annotation.OperationLog;
import com.bigdata.portal.common.exception.CustomException;
import com.bigdata.portal.common.result.R;
import com.bigdata.portal.framework.security.JwtTokenUtil;
import com.bigdata.portal.system.entity.SysUser;
import com.bigdata.portal.system.service.EmailService;
import com.bigdata.portal.system.service.SysUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 * 处理登录、注册等认证相关接口
 * 同时提供 /auth/* 和 /users/* 两套路径（兼容前端）
 */
@RestController
public class AuthController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;

    public AuthController(SysUserService sysUserService, PasswordEncoder passwordEncoder,
                          JwtTokenUtil jwtTokenUtil, EmailService emailService) {
        this.sysUserService = sysUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.emailService = emailService;
    }

    // ======================== 登录 ========================

    /**
     * 用户登录（/auth/login）
     */
    @PostMapping("/auth/login")
    @OperationLog(module = "认证管理", type = "登录", description = "用户登录")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginBody) {
        return doLogin(loginBody);
    }

    /**
     * 用户登录（/users/login - 兼容前端）
     */
    @PostMapping("/users/login")
    public Map<String, Object> loginCompat(@RequestBody Map<String, String> loginBody) {
        try {
            R<Map<String, Object>> result = doLogin(loginBody);
            if (result.getCode() == 200) {
                return successResponse("登录成功", result.getData());
            } else {
                return errorResponse(result.getMsg());
            }
        } catch (CustomException e) {
            return errorResponse(e.getMessage());
        } catch (Exception e) {
            return errorResponse("登录失败，请稍后重试");
        }
    }

    private R<Map<String, Object>> doLogin(Map<String, String> loginBody) {
        String username = loginBody.get("username");
        String password = loginBody.get("password");

        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new CustomException(401, "用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(401, "密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new CustomException(401, "用户已被停用");
        }

        String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());

        return R.ok("登录成功", result);
    }

    // ======================== 发送验证码 ========================

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/users/send-code")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.trim().isEmpty()) {
            return errorResponse("请填写邮箱地址");
        }

        // 简单邮箱格式校验
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return errorResponse("邮箱格式不正确");
        }

        String error = emailService.sendVerificationCode(email.trim());
        if (error != null) {
            return errorResponse(error);
        }

        return successResponse("验证码已发送", null);
    }

    // ======================== 注册 ========================

    /**
     * 用户注册（/auth/register - 旧版无验证码校验）
     */
    @PostMapping("/auth/register")
    @OperationLog(module = "认证管理", type = "注册", description = "用户注册")
    public R<Void> register(@RequestBody SysUser user) {
        sysUserService.save(user);
        return R.ok("注册成功", null);
    }

    /**
     * 用户注册（/users/register - 兼容前端，带验证码校验）
     */
    @PostMapping("/users/register")
    public Map<String, Object> registerWithCode(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        String code = body.get("code");

        // 参数校验
        if (username == null || username.trim().isEmpty()) {
            return errorResponse("请填写用户名");
        }
        if (password == null || password.length() < 6) {
            return errorResponse("密码长度至少6位");
        }
        if (email == null || email.trim().isEmpty()) {
            return errorResponse("请填写邮箱");
        }
        if (code == null || code.trim().isEmpty()) {
            return errorResponse("请填写验证码");
        }

        // 校验验证码
        if (!emailService.verifyCode(email.trim(), code.trim())) {
            return errorResponse("验证码错误或已过期");
        }

        // 检查邮箱是否已注册
        SysUser existByEmail = sysUserService.getByEmail(email.trim());
        if (existByEmail != null) {
            return errorResponse("该邮箱已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setEmail(email.trim());
        user.setNickname(username.trim());
        user.setStatus(0);

        try {
            sysUserService.save(user);
        } catch (CustomException e) {
            return errorResponse(e.getMessage());
        }

        // 注册成功后自动登录
        String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());

        return successResponse("注册成功", result);
    }

    // ======================== 辅助方法 ========================

    private Map<String, Object> successResponse(String msg, Object data) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("msg", msg);
        if (data != null) {
            resp.put("data", data);
        }
        return resp;
    }

    private Map<String, Object> errorResponse(String error) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("error", error);
        return resp;
    }
}
