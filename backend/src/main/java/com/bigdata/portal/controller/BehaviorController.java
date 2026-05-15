package com.bigdata.portal.controller;

import com.bigdata.portal.entity.UserBehavior;
import com.bigdata.portal.kafka.BehaviorProducer;
import com.bigdata.portal.mapper.UserBehaviorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/behaviors")
public class BehaviorController {

    @Autowired
    private BehaviorProducer behaviorProducer;

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    @PostMapping
    public Map<String, Object> report(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object jwtUserIdObj = request.getAttribute("userId");
            Long jwtUserId = jwtUserIdObj != null ? ((Number) jwtUserIdObj).longValue() : null;
            List<Map<String, Object>> events = null;
            if (body.containsKey("events") && body.get("events") instanceof List) {
                events = (List<Map<String, Object>>) body.get("events");
            } else {
                events = Collections.singletonList(body);
            }
            for (Map<String, Object> event : events) {
                if (event == null || event.isEmpty()) continue;
                UserBehavior behavior = new UserBehavior();
                behavior.setEventType((String) event.getOrDefault("eventType", ""));
                Object targetIdObj = event.get("targetId");
                if (targetIdObj instanceof Number) {
                    behavior.setTargetId(((Number) targetIdObj).longValue());
                } else if (targetIdObj instanceof String) {
                    try { behavior.setTargetId(Long.parseLong((String) targetIdObj)); } catch (NumberFormatException e) { behavior.setTargetId(0L); }
                }
                behavior.setTargetType((String) event.getOrDefault("targetType", ""));
                if (jwtUserId != null) {
                    behavior.setUserId(jwtUserId);
                }
                if (behavior.getEventType() != null && !behavior.getEventType().isEmpty()) {
                    behaviorMapper.insert(behavior);
                    behaviorProducer.sendBehavior(behavior);
                }
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
