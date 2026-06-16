package com.bigdata.portal.system.mapper;

import com.bigdata.portal.system.entity.SysMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统菜单Mapper接口
 */
@Mapper
public interface SysMenuMapper {

    @Select("SELECT * FROM sys_menu WHERE menu_id = #{menuId}")
    SysMenu selectById(@Param("menuId") Long menuId);

    @Select("SELECT * FROM sys_menu ORDER BY sort")
    List<SysMenu> selectList();

    @Insert("INSERT INTO sys_menu(parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status, create_time) " +
            "VALUES(#{parentId}, #{menuName}, #{menuType}, #{path}, #{component}, #{permission}, #{icon}, #{sort}, #{visible}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "menuId")
    int insert(SysMenu menu);

    @Update("UPDATE sys_menu SET parent_id=#{parentId}, menu_name=#{menuName}, menu_type=#{menuType}, " +
            "path=#{path}, component=#{component}, permission=#{permission}, icon=#{icon}, " +
            "sort=#{sort}, visible=#{visible}, status=#{status}, update_time=NOW() WHERE menu_id=#{menuId}")
    int update(SysMenu menu);

    @Delete("DELETE FROM sys_menu WHERE menu_id = #{menuId}")
    int deleteById(@Param("menuId") Long menuId);
}
