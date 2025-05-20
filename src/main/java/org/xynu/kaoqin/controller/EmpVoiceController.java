package org.xynu.kaoqin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.entity.EmpVoice;
import org.xynu.kaoqin.entity.Notification;
import org.xynu.kaoqin.mapper.EmpMapper;
import org.xynu.kaoqin.service.EmpVoiceService;
import org.xynu.kaoqin.service.NotificationService;
import org.xynu.kaoqin.util.JwtUtil;

@RestController
@RequestMapping("/voice")
public class EmpVoiceController {
    @Autowired
    private EmpVoiceService empVoiceService;
    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private NotificationService notificationService;

    // 发帖
    @PostMapping("/post")
    public Result<?> post(@RequestBody EmpVoice voice, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        voice.setEmpId(empId);
        empVoiceService.postVoice(voice);
        return Result.success(null);
    }

    // 分页+搜索+显示真实姓名+评论+点赞
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize,
        @RequestParam(required = false) String keyword,
        @RequestHeader(value = "token", required = false) String token
    ) {
        Integer empId = null;
        if (token != null) empId = JwtUtil.getUserId(token);
        return Result.success(empVoiceService.pageList(page, pageSize, keyword, empId));
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        empVoiceService.deleteById(id, empId);
        return Result.success(null);
    }

    // 评论
    @PostMapping("/comment")
    public Result<?> comment(@RequestBody Map<String, Object> map, @RequestHeader("token") String token) {
        Integer commentatorId = JwtUtil.getUserId(token);
        Integer voiceId = (Integer) map.get("voiceId");
        String content = (String) map.get("content");

        EmpVoice empVoice = empVoiceService.getVoiceById(voiceId);
        if (empVoice == null) {
            return Result.fail("心声不存在");
        }

        empVoiceService.comment(voiceId, commentatorId, content);

        if (!commentatorId.equals(empVoice.getEmpId())) {
            Emp commentatorEmp = empMapper.selectById(commentatorId);

            Notification notification = new Notification();
            notification.setSenderId(commentatorId);
            notification.setReceiverId(empVoice.getEmpId());
            notification.setType(2);
            String senderName = (commentatorEmp != null) ? commentatorEmp.getName() : "未知用户";
            notification.setContent(senderName + " 评论了您的心声：" + content);
            notification.setRelatedId(voiceId);
            notification.setIsRead(false);

            notificationService.createNotification(notification);
        }

        return Result.success(null);
    }

    // 点赞/取消点赞
    @PostMapping("/like/{id}")
    public Result<?> like(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        empVoiceService.like(id, empId);
        return Result.success(null);
    }
    @PostMapping("/unlike/{id}")
    public Result<?> unlike(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        empVoiceService.unlike(id, empId);
        return Result.success(null);
    }

    // 删除评论
    @DeleteMapping("/comment/delete/{id}")
    public Result<?> deleteComment(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer empId = JwtUtil.getUserId(token);
        empVoiceService.deleteComment(id, empId);
        return Result.success(null);
    }
}
