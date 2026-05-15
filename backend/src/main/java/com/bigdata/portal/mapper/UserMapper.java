package com.bigdata.portal.mapper;

import com.bigdata.portal.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Insert("INSERT INTO user(username, password, email, role, is_active, created_at) VALUES(#{username}, #{password}, #{email}, #{role}, #{isActive}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE user SET password = #{password} WHERE username = #{username}")
    void updatePassword(@Param("username") String username, @Param("password") String password);

    @Update("UPDATE user SET role = #{role} WHERE id = #{id}")
    void updateRole(@Param("id") Long id, @Param("role") String role);

    @Update("UPDATE user SET is_active = #{isActive} WHERE id = #{id}")
    void updateActive(@Param("id") Long id, @Param("isActive") Boolean isActive);

    @Select("SELECT COUNT(*) FROM user")
    long count();

    @Select("SELECT id, username, email, role, is_active, created_at FROM user ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<User> findPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM user WHERE DATE(created_at) = CURDATE()")
    long countToday();

    @Delete("DELETE FROM user WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
