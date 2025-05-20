package org.xynu.kaoqin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xynu.kaoqin.entity.LeaveRecord;

@Mapper
public interface LeaveRecordMapper {
    int insertLeave(LeaveRecord leave);
    List<LeaveRecord> selectByEmpId(@Param("empId") Integer empId);
    List<LeaveRecord> selectAll();
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    LeaveRecord selectById(@Param("id") Integer id);
}
