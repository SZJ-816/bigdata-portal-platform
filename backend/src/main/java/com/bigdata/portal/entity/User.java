package com.bigdata.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String role;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;
}
