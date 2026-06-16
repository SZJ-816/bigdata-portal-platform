package com.bigdata.portal.system.mapper;

import com.bigdata.portal.system.entity.SysConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统配置Mapper接口
 */
@Mapper
public interface SysConfigMapper {

    @Select("SELECT * FROM sys_config WHERE config_id = #{configId}")
    SysConfig selectById(@Param("configId") Long configId);

    @Select("SELECT * FROM sys_config WHERE config_key = #{configKey}")
    SysConfig selectByKey(@Param("configKey") String configKey);

    @Select("SELECT * FROM sys_config ORDER BY create_time DESC")
    List<SysConfig> selectList();

    @Insert("INSERT INTO sys_config(config_name, config_key, config_value, is_system, create_time, remark) " +
            "VALUES(#{configName}, #{configKey}, #{configValue}, #{isSystem}, NOW(), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "configId")
    int insert(SysConfig config);

    @Update("UPDATE sys_config SET config_name=#{configName}, config_key=#{configKey}, " +
            "config_value=#{configValue}, is_system=#{isSystem}, update_time=NOW(), remark=#{remark} " +
            "WHERE config_id=#{configId}")
    int update(SysConfig config);

    @Delete("DELETE FROM sys_config WHERE config_id = #{configId}")
    int deleteById(@Param("configId") Long configId);
}
