package com.bigdata.portal.kafka;

import com.alibaba.fastjson.JSON;
import com.bigdata.portal.entity.UserBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BehaviorProducer {

    private static final String TOPIC = "user-behavior";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendBehavior(UserBehavior behavior) {
        String json = JSON.toJSONString(behavior);
        kafkaTemplate.send(TOPIC, String.valueOf(behavior.getUserId()), json);
    }
}
