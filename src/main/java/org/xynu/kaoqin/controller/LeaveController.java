package org.xynu.kaoqin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.entity.LeaveRecord;
import org.xynu.kaoqin.entity.Notification;
import org.xynu.kaoqin.mapper.EmpMapper;
import org.xynu.kaoqin.service.LeaveService;
import org.xynu.kaoqin.service.NotificationService;
import org.xynu.kaoqin.util.JwtUtil;

@RestController
@RequestMapping("/leave")
public class LeaveController {
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private NotificationService notificationService;

    // 员工申请请假
    @PostMapping("/apply")
    public Result<?> apply(@RequestBody LeaveRecord leave, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        leave.setEmpId(empId);
        leaveService.applyLeave(leave);
        return Result.success(null);
    }

    // 员工查看自己的请假
    @GetMapping("/my")
    public Result<List<LeaveRecord>> myLeaves(@RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        return Result.success(leaveService.getMyLeaves(empId));
    }

    // 管理员查看所有请假
    @GetMapping("/all")
    public Result<List<LeaveRecord>> allLeaves(@RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        Emp emp = empMapper.selectById(empId);
        if (emp == null || !"admin".equals(emp.getUsername())) {
            return Result.fail("无权限操作");
        }
        return Result.success(leaveService.getAllLeaves());
    }

    // 管理员审批
    @PostMapping("/approve")
    public Result<?> approve(@RequestParam Integer id, @RequestParam Integer status, @RequestHeader("token") String token) {
        Integer adminId = JwtUtil.getUserId(token);
        Emp adminEmp = empMapper.selectById(adminId);
        if (adminEmp == null || !"admin".equals(adminEmp.getUsername())) {
            return Result.fail("无权限操作");
        }

        LeaveRecord leaveRecord = leaveService.getLeaveRecordById(id);
        if (leaveRecord == null) {
             return Result.fail("请假记录不存在");
        }

        leaveService.approveLeave(id, status);

        Notification notification = new Notification();
        notification.setSenderId(adminId);
        notification.setReceiverId(leaveRecord.getEmpId());
        notification.setType(1);
        String statusText = (status == 1) ? "通过" : "拒绝";
        notification.setContent("您的请假申请（" + leaveRecord.getReason() + "）已被" + statusText);
        notification.setRelatedId(id);
        notification.setIsRead(false);

        notificationService.createNotification(notification);

        return Result.success(null);
    }
}
