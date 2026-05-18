package com.bigdata.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        String token = jwtConfig.extractToken(authHeader);
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/analytics/")) {
            if (token != null && jwtConfig.validateToken(token)) {
                Long userId = jwtConfig.getUserIdFromToken(token);
                request.setAttribute("userId", userId);
            }
            return true;
        }

        if (uri.startsWith("/api/admin/")) {
            if (token != null && jwtConfig.validateToken(token)) {
                Long userId = jwtConfig.getUserIdFromToken(token);
                request.setAttribute("userId", userId);
                String role = jwtConfig.getRoleFromToken(token);
                request.setAttribute("userRole", role);
            }
            return true;
        }

        if (uri.startsWith("/api/ai/")) {
            if (token != null && jwtConfig.validateToken(token)) {
                Long userId = jwtConfig.getUserIdFromToken(token);
                request.setAttribute("userId", userId);
            }
            return true;
        }

        if (uri.startsWith("/api/news/") || uri.startsWith("/api/channel") || uri.startsWith("/api/users/favorites") || uri.startsWith("/api/behaviors") || uri.startsWith("/api/image/")) {
            if (token != null && jwtConfig.validateToken(token)) {
                Long userId = jwtConfig.getUserIdFromToken(token);
                request.setAttribute("userId", userId);
            }
            return true;
        }

        if (token == null || !jwtConfig.validateToken(token)) {
            sendError(response, 401, "未登录或token无效");
            return false;
        }
        Long userId = jwtConfig.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        return true;
    }

    private void sendError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
