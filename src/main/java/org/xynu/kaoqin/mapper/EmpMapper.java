package org.xynu.kaoqin.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xynu.kaoqin.entity.DeptCountVO;
import org.xynu.kaoqin.entity.Emp;

@Mapper
public interface EmpMapper {
    // 分页+条件查询
    List<Emp> selectByPage(@Param("name") String name, @Param("gender") Integer gender,
                           @Param("begin") LocalDate begin, @Param("end") LocalDate end);

    // 查询总数
    Long countByPage(@Param("name") String name, @Param("gender") Integer gender,
                     @Param("begin") LocalDate begin, @Param("end") LocalDate end);

    // 查询所有
    List<Emp> selectAll();
    Emp selectByUsername(String username);
    // 根据ID查
    Emp selectById(Integer id);

    // 新增
    int insert(Emp emp);

    // 修改
    int update(Emp emp);

    // 批量删除
    int deleteByIds(@Param("ids") List<Integer> ids);

    int countAll();
    int countByGender(@Param("gender") int gender);
    List<Emp> findLatest(@Param("limit") int limit);
    List<DeptCountVO> countByDept();

    List<Emp> selectEmps(
        @Param("name") String name,
        @Param("gender") Integer gender,
        @Param("begin") String begin,
        @Param("end") String end,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize,
        @Param("sortField") String sortField,
        @Param("sortOrder") String sortOrder
    );

    Long countEmps(
        @Param("name") String name,
        @Param("gender") Integer gender,
        @Param("begin") String begin,
        @Param("end") String end
    );
}