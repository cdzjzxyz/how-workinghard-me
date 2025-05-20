package org.xynu.kaoqin.controller;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Attendance;
import org.xynu.kaoqin.service.AttendanceService;
import org.xynu.kaoqin.util.JwtUtil;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/checkin")
    public Result<?> checkIn(@RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        attendanceService.checkIn(empId);
        return Result.success(null);
    }

    @PostMapping("/checkout")
    public Result<?> checkOut(@RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        attendanceService.checkOut(empId);
        return Result.success(null);
    }

    @GetMapping("/today")
    public Result<Attendance> getToday(@RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        return Result.success(attendanceService.getTodayAttendance(empId));
    }

    @GetMapping("/my")
    public Result<List<Attendance>> getMy(@RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        return Result.success(attendanceService.getMyAttendance(empId));
    }

    @GetMapping("/stats")
    public Result<Map<String, Integer>> getStats(@RequestHeader("token") String token, @RequestParam String month) {
        Integer empId = JwtUtil.getUserId(token);
        return Result.success(attendanceService.getStats(empId, month));
    }

    @GetMapping("/calendar")
    public Result<List<Attendance>> getCalendar(@RequestHeader("token") String token, @RequestParam String month) {
        Integer empId = JwtUtil.getUserId(token);
        return Result.success(attendanceService.getCalendar(empId, month));
    }

    @GetMapping("/report")
    public Result<?> getAttendanceReport(@RequestHeader("token") String token, @RequestParam String month) {
        Integer empId = JwtUtil.getUserId(token);
        // 获取明细
        List<Attendance> list = attendanceService.getCalendar(empId, month);
        // 统计
        Map<String, Integer> summary = attendanceService.getStats(empId, month);
        // 字段适配前端
        List<Map<String, Object>> detail = list.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("date", a.getDate());
            m.put("checkIn", a.getCheckInTime() == null ? "" : a.getCheckInTime().toString().replace("T", " "));
            m.put("checkOut", a.getCheckOutTime() == null ? "" : a.getCheckOutTime().toString().replace("T", " "));
            m.put("status", a.getStatus());
            m.put("performanceScore", a.getPerformanceScore());
            return m;
        }).toList();
        Map<String, Object> data = new HashMap<>();
        data.put("summary", summary);
        data.put("detail", detail);
        return Result.success(data);
    }

    @GetMapping("/export")
    public void exportAttendance(
        @RequestHeader("token") String token,
        @RequestParam String month,
        jakarta.servlet.http.HttpServletResponse response
    ) throws Exception {
        Integer empId = JwtUtil.getUserId(token);
        List<Attendance> list = attendanceService.getCalendar(empId, month);

        // 创建Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("考勤报表");
        // 表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("日期");
        header.createCell(1).setCellValue("签到时间");
        header.createCell(2).setCellValue("签退时间");
        header.createCell(3).setCellValue("状态");
        header.createCell(4).setCellValue("绩效分数");
        // 数据
        int rowIdx = 1;
        for (Attendance a : list) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(a.getDate() == null ? "" : a.getDate().toString());
            row.createCell(1).setCellValue(a.getCheckInTime() == null ? "" : a.getCheckInTime().toString().replace("T", " "));
            row.createCell(2).setCellValue(a.getCheckOutTime() == null ? "" : a.getCheckOutTime().toString().replace("T", " "));
            String statusText = "";
            switch (a.getStatus() == null ? 0 : a.getStatus()) {
                case 1: statusText = "正常"; break;
                case 2: statusText = "迟到"; break;
                case 3: statusText = "早退"; break;
                case 4: statusText = "缺卡"; break;
                case 5: statusText = "请假"; break;
                default: statusText = "";
            }
            row.createCell(3).setCellValue(statusText);
            row.createCell(4).setCellValue(a.getPerformanceScore() == null ? "" : a.getPerformanceScore().toString());
        }
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("考勤报表_" + month + ".xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/month-score")
    public Result<Integer> getMonthScore(@RequestHeader("token") String token, @RequestParam String month) {
        Integer empId = JwtUtil.getUserId(token);
        Integer score = attendanceService.getMonthPerformanceScore(empId, month);
        return Result.success(score);
    }
}
