package com.bigdata.portal.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserFavorite {
    private Long id;
    private Long userId;
    private Long newsId;
    private Date createdAt;
}
