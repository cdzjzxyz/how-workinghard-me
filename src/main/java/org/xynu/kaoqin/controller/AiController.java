package org.xynu.kaoqin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xynu.kaoqin.service.AttendanceService;
import org.xynu.kaoqin.util.OpenRouterUtil;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/analyze")
    public String analyze(@RequestBody Map<String, Object> params) {
        String question = (String) params.get("question");
        // 你可以把考勤数据拼接到prompt里
        try {
            String result = OpenRouterUtil.askModel(question);
            return result;
        } catch (Exception e) {
            return "AI分析失败：" + e.getMessage();
        }
    }

    @PostMapping("/analyzeAttendance")
    public String analyzeAttendance() {
        // 查询考勤最差员工数据
        java.util.List<java.util.Map<String, Object>> badList = attendanceService.getBadAttendanceList();
        System.out.println("badList数据：" + badList);
        StringBuilder sb = new StringBuilder();
        sb.append("以下是公司考勤最差的员工名单：\n");
        sb.append("工号\t姓名\t部门\t迟到次数\t早退次数\t缺勤次数\t请假次数\n");
        for (java.util.Map<String, Object> row : badList) {
            sb.append(row.get("emp_id")).append("\t")
              .append(row.get("emp_name")).append("\t")
              .append(row.get("dept_id")).append("\t")
              .append(row.get("late_count")).append("\t")
              .append(row.get("early_leave_count")).append("\t")
              .append(row.get("absent_count")).append("\t")
              .append(row.get("leave_count")).append("\n");
        }
        sb.append("请根据这些数据，分析谁的考勤绩效最差，并给出改进建议。");
        String prompt = sb.toString();
        System.out.println("AI分析prompt内容：\n" + prompt);
        try {
            return OpenRouterUtil.askModel(prompt);
        } catch (Exception e) {
            return "AI分析失败：" + e.getMessage();
        }
    }
}