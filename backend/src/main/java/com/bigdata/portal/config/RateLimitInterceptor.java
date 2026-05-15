package com.bigdata.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, RateBucket> buckets = new ConcurrentHashMap<>();
    private static final int AI_LIMIT = 10;
    private static final int CODE_LIMIT = 1;
    private static final int WINDOW_SECONDS = 60;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String clientId = getClientId(request);
        int limit = getLimit(uri);
        if (limit <= 0) return true;

        String key = clientId + ":" + uri;
        RateBucket bucket = buckets.computeIfAbsent(key, k -> new RateBucket());
        if (!bucket.tryAcquire(limit)) {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("success", false);
            body.put("error", "请求过于频繁，请稍后再试");
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            return false;
        }
        return true;
    }

    private String getClientId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && !auth.isEmpty()) return auth;
        return request.getRemoteAddr();
    }

    private int getLimit(String uri) {
        if (uri.startsWith("/api/ai/")) return AI_LIMIT;
        if (uri.equals("/api/users/send-code")) return CODE_LIMIT;
        return -1;
    }

    private static class RateBucket {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        boolean tryAcquire(int limit) {
            long now = System.currentTimeMillis();
            if (now - windowStart > WINDOW_SECONDS * 1000) {
                synchronized (this) {
                    if (now - windowStart > WINDOW_SECONDS * 1000) {
                        windowStart = now;
                        count.set(0);
                    }
                }
            }
            return count.incrementAndGet() <= limit;
        }
    }
}
