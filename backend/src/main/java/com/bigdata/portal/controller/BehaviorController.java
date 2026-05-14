package com.bigdata.portal.controller;

import com.bigdata.portal.entity.UserBehavior;
import com.bigdata.portal.kafka.BehaviorProducer;
import com.bigdata.portal.mapper.UserBehaviorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/behaviors")
public class BehaviorController {

    @Autowired
    private BehaviorProducer behaviorProducer;

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    @PostMapping
    public Map<String, Object> report(@RequestBody UserBehavior behavior) {
        Map<String, Object> result = new HashMap<>();
        try {
            behaviorMapper.insert(behavior);
            behaviorProducer.sendBehavior(behavior);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", true);
        }
        return result;
    }
}
