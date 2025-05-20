package org.xynu.kaoqin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.xynu.kaoqin.entity.Dept;

@Mapper
public interface DeptMapper {
    @Select("SELECT * FROM dept")
    List<Dept> findAll();

    @Select("SELECT * FROM dept WHERE id = #{id}")
    Dept findById(Integer id);

    @Insert("INSERT INTO dept(name) VALUES(#{name})")
    int insert(Dept dept);

    @Update("UPDATE dept SET name = #{name}, update_time = NOW() WHERE id = #{id}")
    int update(Dept dept);

    @Delete("DELETE FROM dept WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT COUNT(*) FROM dept")
    int countAll();
} 