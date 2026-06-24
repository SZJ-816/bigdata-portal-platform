package com.bigdata.portal.ai.mapper;

import com.bigdata.portal.ai.entity.AiModelConfig;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI模型配置数据访问
 */
@Repository
public class AiModelConfigMapper {

    private final JdbcTemplate jdbcTemplate;

    public AiModelConfigMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AiModelConfig> findAll() {
        String sql = "SELECT * FROM ai_model_config ORDER BY is_default DESC, create_time DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AiModelConfig.class));
    }

    public AiModelConfig findById(Long id) {
        String sql = "SELECT * FROM ai_model_config WHERE id = ?";
        List<AiModelConfig> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AiModelConfig.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public AiModelConfig findDefault() {
        String sql = "SELECT * FROM ai_model_config WHERE is_default = 1 AND status = 1 LIMIT 1";
        List<AiModelConfig> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AiModelConfig.class));
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(AiModelConfig config) {
        String sql = "INSERT INTO ai_model_config (name, provider, api_url, api_key, model_name, temperature, max_tokens, top_p, status, is_default, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql,
                config.getName(), config.getProvider(), config.getApiUrl(), config.getApiKey(),
                config.getModelName(), config.getTemperature(), config.getMaxTokens(), config.getTopP(),
                config.getStatus() != null ? config.getStatus() : 1,
                config.getIsDefault() != null ? config.getIsDefault() : 0);
    }

    public int update(AiModelConfig config) {
        String sql = "UPDATE ai_model_config SET name=?, provider=?, api_url=?, api_key=?, model_name=?, temperature=?, max_tokens=?, top_p=?, status=?, is_default=?, update_time=NOW() WHERE id=?";
        return jdbcTemplate.update(sql,
                config.getName(), config.getProvider(), config.getApiUrl(), config.getApiKey(),
                config.getModelName(), config.getTemperature(), config.getMaxTokens(), config.getTopP(),
                config.getStatus(), config.getIsDefault(), config.getId());
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM ai_model_config WHERE id = ?", id);
    }

    public int clearDefault() {
        return jdbcTemplate.update("UPDATE ai_model_config SET is_default = 0 WHERE is_default = 1");
    }

    public int setDefault(Long id) {
        return jdbcTemplate.update("UPDATE ai_model_config SET is_default = 1 WHERE id = ?", id);
    }
}
