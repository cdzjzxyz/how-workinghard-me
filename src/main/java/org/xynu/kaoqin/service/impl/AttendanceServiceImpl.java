package org.xynu.kaoqin.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Attendance;
import org.xynu.kaoqin.mapper.AttendanceMapper;
import org.xynu.kaoqin.service.AttendanceService;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public void checkIn(Integer empId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 查询最近一次打卡日期
        LocalDate lastDate = attendanceMapper.selectLastAttendanceDate(empId);
        if (lastDate != null && lastDate.isBefore(today.minusDays(1))) {
            // 补全断档天数
            LocalDate fillDate = lastDate.plusDays(1);
            while (fillDate.isBefore(today)) {
                Integer lastScore = attendanceMapper.selectLastScore(empId, fillDate);
                if (lastScore == null) lastScore = 100;
                int score = lastScore - 5;
                attendanceMapper.insertCheckIn(empId, fillDate, null, 4, score);
                fillDate = fillDate.plusDays(1);
            }
        }
        // 查询最近一次有记录的分数
        Integer lastScore = attendanceMapper.selectLastScore(empId, today);
        if (lastScore == null) {
            lastScore = 100;
        }
        int status = 1; // 实际应根据业务判断
        int score = lastScore;
        switch(status) {
            case 2: score -= 2; break; // 迟到
            case 3: score -= 2; break; // 早退
            case 4: score -= 5; break; // 缺卡
            case 5: score -= 1; break; // 请假
        }
        attendanceMapper.insertCheckIn(empId, today, now, status, score);
    }

    @Override
    public void checkOut(Integer empId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        attendanceMapper.updateCheckOut(empId, today, now);
    }

    @Override
    public Attendance getTodayAttendance(Integer empId) {
        return attendanceMapper.selectByEmpIdAndDate(empId, LocalDate.now());
    }

    @Override
    public List<Attendance> getMyAttendance(Integer empId) {
        return attendanceMapper.selectByEmpId(empId);
    }

    @Override
    public Map<String, Integer> getStats(Integer empId, String month) {
        List<Attendance> list = attendanceMapper.selectByEmpIdAndMonth(empId, month);
        int attend = 0, late = 0, early = 0, absent = 0, leave = 0;
        for (Attendance a : list) {
            switch (a.getStatus()) {
                case 1: attend++; break;
                case 2: late++; break;
                case 3: early++; break;
                case 4: absent++; break;
                case 5: leave++; break;
            }
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("attend", attend);
        map.put("late", late);
        map.put("early", early);
        map.put("absent", absent);
        map.put("leave", leave);
        return map;
    }

    @Override
    public List<Attendance> getCalendar(Integer empId, String month) {
        return attendanceMapper.selectByEmpIdAndMonth(empId, month);
    }

    @Override
    public Integer getMonthPerformanceScore(Integer empId, String month) {
        List<Attendance> list = attendanceMapper.selectByEmpIdAndMonth(empId, month);
        if (list == null || list.isEmpty()) return null;
        // 取最后一天的分数
        return list.get(list.size() - 1).getPerformanceScore();
    }

    @Override
    public List<Map<String, Object>> getBadAttendanceList() {
        return attendanceMapper.selectBadAttendance();
    }

    /**
     * 每天23:59自动补全未打卡员工的缺勤记录
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void autoFillAbsence() {
        LocalDate today = LocalDate.now();
        // 查询所有员工ID（假设有empMapper，或你有获取所有员工的方法）
        List<Integer> empIds = attendanceMapper.selectAllEmpIds();
        for (Integer empId : empIds) {
            // 如果今天没有考勤记录，则补一条缺勤
            if (attendanceMapper.selectByEmpIdAndDate(empId, today) == null) {
                // 查前一天分数
                Integer lastScore = attendanceMapper.selectLastScore(empId, today.minusDays(1));
                if (lastScore == null) lastScore = 100;
                int score = lastScore - 5; // 缺勤扣5分
                attendanceMapper.insertCheckIn(empId, today, null, 4, score);
            }
        }
        System.out.println("自动补全缺勤任务执行完毕：" + today);
    }

    @Override
    public Result aiAnalysis(Integer empId) {
        // 查询员工最近30天的考勤记录
        List<Attendance> attendanceList = attendanceMapper.selectByEmpIdAndDateRange(empId, LocalDate.now().minusDays(30), LocalDate.now());
        
        // 提取考勤数据
        List<Map<String, Object>> data = attendanceList.stream()
            .map(record -> {
                Map<String, Object> map = new HashMap<>();
                map.put("date", record.getDate());
                map.put("checkInTime", record.getCheckInTime());
                map.put("checkOutTime", record.getCheckOutTime());
                map.put("status", record.getStatus());
                map.put("score", record.getPerformanceScore());
                return map;
            })
            .collect(Collectors.toList());
        
        // 调用AI接口分析数据
        String aiResult = callAiApi(data);
        
        // 返回AI分析结果
        return Result.success(aiResult);
    }
    
    private String callAiApi(List<Map<String, Object>> data) {
        // 这里调用AI接口，比如OpenAI、百度AI等
        // 示例：调用OpenAI的API
        // 具体实现略
        return "AI分析结果";
    }
}
