package com.bigdata.portal.system.mapper;

import com.bigdata.portal.system.entity.SysDictData;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 字典数据Mapper接口
 */
@Mapper
public interface SysDictDataMapper {

    @Select("SELECT * FROM sys_dict_data WHERE dict_data_id = #{dictDataId}")
    SysDictData selectById(@Param("dictDataId") Long dictDataId);

    @Select("SELECT * FROM sys_dict_data WHERE dict_type = #{dictType} ORDER BY sort")
    List<SysDictData> selectByDictType(@Param("dictType") String dictType);

    @Select("SELECT * FROM sys_dict_data ORDER BY dict_type, sort")
    List<SysDictData> selectList();

    @Insert("INSERT INTO sys_dict_data(dict_type, dict_label, dict_value, sort, status, create_time, remark) " +
            "VALUES(#{dictType}, #{dictLabel}, #{dictValue}, #{sort}, #{status}, NOW(), #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "dictDataId")
    int insert(SysDictData dictData);

    @Update("UPDATE sys_dict_data SET dict_type=#{dictType}, dict_label=#{dictLabel}, " +
            "dict_value=#{dictValue}, sort=#{sort}, status=#{status}, update_time=NOW(), remark=#{remark} " +
            "WHERE dict_data_id=#{dictDataId}")
    int update(SysDictData dictData);

    @Delete("DELETE FROM sys_dict_data WHERE dict_data_id = #{dictDataId}")
    int deleteById(@Param("dictDataId") Long dictDataId);
}
