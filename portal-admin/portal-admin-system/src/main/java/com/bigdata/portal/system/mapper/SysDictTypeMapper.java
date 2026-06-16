package com.bigdata.portal.system.mapper;

import com.bigdata.portal.system.entity.SysDictType;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 字典类型Mapper接口
 */
@Mapper
public interface SysDictTypeMapper {

    @Select("SELECT * FROM sys_dict_type WHERE dict_type_id = #{dictTypeId}")
    SysDictType selectById(@Param("dictTypeId") Long dictTypeId);

    @Select("SELECT * FROM sys_dict_type ORDER BY create_time DESC")
    List<SysDictType> selectList();

    @Insert("INSERT INTO sys_dict_type(dict_name, dict_type, status, create_time, remark) " +
            "VALUES(#{dictName}, #{dictType}, #{status}, NOW(), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "dictTypeId")
    int insert(SysDictType dictType);

    @Update("UPDATE sys_dict_type SET dict_name=#{dictName}, dict_type=#{dictType}, " +
            "status=#{status}, update_time=NOW(), remark=#{remark} WHERE dict_type_id=#{dictTypeId}")
    int update(SysDictType dictType);

    @Delete("DELETE FROM sys_dict_type WHERE dict_type_id = #{dictTypeId}")
    int deleteById(@Param("dictTypeId") Long dictTypeId);
}
