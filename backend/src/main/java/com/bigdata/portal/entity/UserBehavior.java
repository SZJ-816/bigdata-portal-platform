package com.bigdata.portal.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserBehavior {
    private Long id;
    private Long userId;
    private String eventType;
    private Long targetId;
    private String targetType;
    private String extra;
    private Date createdAt;
}
