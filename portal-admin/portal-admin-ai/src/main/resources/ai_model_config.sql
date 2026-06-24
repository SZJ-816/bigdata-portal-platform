-- AI模型配置表
CREATE TABLE IF NOT EXISTS `ai_model_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '配置名称',
  `provider` VARCHAR(50) NOT NULL DEFAULT 'nvidia' COMMENT '提供商(nvidia/openai/other)',
  `api_url` VARCHAR(500) NOT NULL COMMENT 'API地址',
  `api_key` VARCHAR(500) NOT NULL COMMENT 'API密钥',
  `model_name` VARCHAR(200) NOT NULL COMMENT '模型名称',
  `temperature` DOUBLE DEFAULT 0.6 COMMENT '温度参数',
  `max_tokens` INT DEFAULT 16384 COMMENT '最大token数',
  `top_p` DOUBLE DEFAULT 0.95 COMMENT 'Top P参数',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用/1启用)',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认(0否/1是)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模型配置表';

-- 插入默认配置（从application.yml迁移）
INSERT IGNORE INTO `ai_model_config` (`name`, `provider`, `api_url`, `api_key`, `model_name`, `temperature`, `max_tokens`, `top_p`, `status`, `is_default`)
VALUES ('NVIDIA Qwen3.5', 'nvidia', 'https://integrate.api.nvidia.com/v1/chat/completions', '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', 'qwen/qwen3.5-122b-a10b', 0.6, 16384, 0.95, 1, 1);
