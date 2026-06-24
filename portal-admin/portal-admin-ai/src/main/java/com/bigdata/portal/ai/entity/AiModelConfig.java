package com.bigdata.portal.ai.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI模型配置实体
 */
@Data
public class AiModelConfig {
    private Long id;
    private String name;
    private String provider;
    private String apiUrl;
    private String apiKey;
    private String modelName;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Integer status;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
