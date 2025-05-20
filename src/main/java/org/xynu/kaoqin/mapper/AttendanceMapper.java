package org.xynu.kaoqin.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.xynu.kaoqin.entity.Attendance;

@Mapper
public interface AttendanceMapper {
    int insertCheckIn(@Param("empId") Integer empId, 
                      @Param("date") LocalDate date, 
                      @Param("checkInTime") LocalDateTime checkInTime,
                      @Param("status") Integer status,
                      @Param("performanceScore") Integer performanceScore);
    int updateCheckOut(@Param("empId") Integer empId, @Param("date") LocalDate date, @Param("checkOutTime") LocalDateTime checkOutTime);
    Attendance selectByEmpIdAndDate(@Param("empId") Integer empId, @Param("date") LocalDate date);
    List<Attendance> selectByEmpId(@Param("empId") Integer empId);
    List<Attendance> selectByDeptIdAndDate(@Param("deptId") Integer deptId, @Param("date") LocalDate date);
    List<Attendance> selectByEmpIdAndMonth(@Param("empId") Integer empId, @Param("month") String month);
    /**
     * 查询员工最近一次有记录的绩效分数
     * @param empId 员工ID
     * @param date 当前日期
     * @return 最近一次有记录的绩效分数
     */
    Integer selectLastScore(@Param("empId") Integer empId, @Param("date") LocalDate date);
    List<java.util.Map<String, Object>> selectBadAttendance();
    @Select("SELECT id FROM emp")
    List<Integer> selectAllEmpIds();
    /**
     * 查询某员工最近一次打卡日期
     */
    java.time.LocalDate selectLastAttendanceDate(@org.apache.ibatis.annotations.Param("empId") Integer empId);
    List<Attendance> selectByEmpIdAndDateRange(@Param("empId") Integer empId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
