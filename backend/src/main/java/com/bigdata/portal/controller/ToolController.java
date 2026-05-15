package com.bigdata.portal.controller;

import com.bigdata.portal.config.JwtConfig;
import com.bigdata.portal.entity.User;
import com.bigdata.portal.mapper.UserMapper;
import com.bigdata.portal.service.RssService;
import com.bigdata.portal.util.SummaryUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    @Autowired
    private RssService rssService;

    @Autowired
    private SummaryUpdater summaryUpdater;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/fetch-news")
    public Map<String, Object> fetchNews() {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = rssService.fetchAndSave();
            result.put("success", true);
            result.put("message", "成功抓取 " + count + " 条新闻");
            result.put("count", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "抓取失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/update-summaries")
    public Map<String, Object> updateSummaries() {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = summaryUpdater.updateAllInvalidSummaries();
            result.put("success", true);
            result.put("message", "成功更新 " + count + " 条摘要");
            result.put("count", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = params.get("username");
            String password = params.get("password");

            String authHeader = request.getHeader("Authorization");
            String token = jwtConfig.extractToken(authHeader);
            if (token == null || !"admin".equals(jwtConfig.getRoleFromToken(token))) {
                result.put("success", false);
                result.put("error", "权限不足");
                return result;
            }
            
            if (username == null || password == null) {
                result.put("success", false);
                result.put("message", "请提供用户名和密码");
                return result;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode(password);
            
            User user = userMapper.findByUsername(username);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            userMapper.updatePassword(username, hashedPassword);
            result.put("success", true);
            result.put("message", "密码重置成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重置失败: " + e.getMessage());
        }
        return result;
    }
}