package com.bigdata.portal.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private Long newsId;
    private String content;
    private Long parentId;
    private Integer likeCount;
    private Date createdAt;
}
