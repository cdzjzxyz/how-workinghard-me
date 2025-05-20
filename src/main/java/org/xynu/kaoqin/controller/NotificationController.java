package org.xynu.kaoqin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Notification;
import org.xynu.kaoqin.service.NotificationService;
import org.xynu.kaoqin.util.JwtUtil;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 获取当前用户的通知列表
    @GetMapping("/my")
    public Result<List<Notification>> getMyNotifications(@RequestHeader("token") String token) {
        Integer userId = JwtUtil.getUserId(token);
        List<Notification> notifications = notificationService.getNotificationsByReceiverId(userId);
        return Result.success(notifications);
    }

    // 标记通知为已读
    @PostMapping("/markAsRead/{id}")
    public Result<?> markAsRead(@PathVariable Integer id, @RequestHeader("token") String token) {
         Integer userId = JwtUtil.getUserId(token);
         // 可以在这里添加权限校验，确保用户只能标记自己的通知
         // Notification notification = notificationService.getNotificationById(id); // 如果有这个方法
         // if (notification != null && notification.getReceiverId().equals(userId)) {
               notificationService.markAsRead(id);
               return Result.success(null);
         // } else {
         //    return Result.fail("无权限操作或通知不存在");
         // }

    }

     // 获取未读通知数量 (可选)
    @GetMapping("/unreadCount")
    public Result<Integer> getUnreadCount(@RequestHeader("token") String token) {
         Integer userId = JwtUtil.getUserId(token);
         int count = notificationService.countUnreadNotifications(userId);
         return Result.success(count);
    }

    // 删除通知 (可选)
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteNotification(@PathVariable Integer id, @RequestHeader("token") String token) {
        Integer userId = JwtUtil.getUserId(token);
        // 可以在这里添加权限校验，确保用户只能删除自己的通知
         // Notification notification = notificationService.getNotificationById(id); // 如果有这个方法
         // if (notification != null && notification.getReceiverId().equals(userId)) {
               notificationService.deleteById(id); // 假设NotificationService有deleteById方法
               return Result.success(null);
         // } else {
         //    return Result.fail("无权限操作或通知不存在");
         // }
    }
} 