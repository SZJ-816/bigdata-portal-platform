package com.bigdata.portal.entity;

import lombok.Data;
import java.util.Date;

@Data
public class News {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String channel;
    private String source;
    private String imageUrl;
    private Integer viewCount;
    private Integer commentCount;
    private Integer isBreaking;
    private Date publishTime;
    private Date createdAt;
    private Date updatedAt;
    private String sourceUrl;
}
