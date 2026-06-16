package com.bigdata.portal.system.mapper;

import com.bigdata.portal.system.entity.SysRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统角色Mapper接口
 */
@Mapper
public interface SysRoleMapper {

    @Select("SELECT * FROM sys_role WHERE role_id = #{roleId}")
    SysRole selectById(@Param("roleId") Long roleId);

    @Select("SELECT * FROM sys_role ORDER BY sort")
    List<SysRole> selectList();

    @Insert("INSERT INTO sys_role(role_name, role_key, sort, status, create_time, remark) " +
            "VALUES(#{roleName}, #{roleKey}, #{sort}, #{status}, NOW(), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "roleId")
    int insert(SysRole role);

    @Update("UPDATE sys_role SET role_name=#{roleName}, role_key=#{roleKey}, sort=#{sort}, " +
            "status=#{status}, update_time=NOW(), remark=#{remark} WHERE role_id=#{roleId}")
    int update(SysRole role);

    @Delete("DELETE FROM sys_role WHERE role_id = #{roleId}")
    int deleteById(@Param("roleId") Long roleId);
}
