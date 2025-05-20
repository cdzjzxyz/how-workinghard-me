package org.xynu.kaoqin.service;

import java.util.List;
import java.util.Map;

import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Attendance;

public interface AttendanceService {
    /**
     * 上班打卡，自动递推累计绩效分数
     */
    void checkIn(Integer empId);
    void checkOut(Integer empId);
    Attendance getTodayAttendance(Integer empId);
    List<Attendance> getMyAttendance(Integer empId);
    Map<String, Integer> getStats(Integer empId, String month);
    List<Attendance> getCalendar(Integer empId, String month);
    /**
     * 获取某员工某月最后一天的绩效分数
     */
    Integer getMonthPerformanceScore(Integer empId, String month);
    List<Map<String, Object>> getBadAttendanceList();
    Result aiAnalysis(Integer empId);
}
