package com.bigdata.portal.system.mapper;

import com.bigdata.portal.system.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统用户Mapper接口
 */
@Mapper
public interface SysUserMapper {

    /** 根据用户ID查询 */
    @Select("SELECT * FROM sys_user WHERE user_id = #{userId}")
    SysUser selectById(@Param("userId") Long userId);

    /** 根据用户名查询 */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(@Param("username") String username);

    /** 根据邮箱查询 */
    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUser selectByEmail(@Param("email") String email);

    /** 查询用户列表 */
    @Select("SELECT * FROM sys_user ORDER BY create_time DESC")
    List<SysUser> selectList();

    /** 新增用户 */
    @Insert("INSERT INTO sys_user(username, password, nickname, email, phone, gender, avatar, status, create_time, remark) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{email}, #{phone}, #{gender}, #{avatar}, #{status}, NOW(), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(SysUser user);

    /** 更新用户 */
    @Update("UPDATE sys_user SET nickname=#{nickname}, email=#{email}, phone=#{phone}, " +
            "gender=#{gender}, avatar=#{avatar}, status=#{status}, update_time=NOW(), remark=#{remark} " +
            "WHERE user_id=#{userId}")
    int update(SysUser user);

    /** 删除用户 */
    @Delete("DELETE FROM sys_user WHERE user_id = #{userId}")
    int deleteById(@Param("userId") Long userId);
}
