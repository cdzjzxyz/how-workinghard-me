package org.xynu.kaoqin.service;

import java.util.List;

import org.xynu.kaoqin.entity.Notification;

public interface NotificationService {
    // 创建通知
    void createNotification(Notification notification);

    // 根据接收者ID获取通知列表
    List<Notification> getNotificationsByReceiverId(Integer receiverId);

    // 标记通知为已读
    void markAsRead(Integer notificationId);

    // 根据接收者ID获取未读通知数量
    int countUnreadNotifications(Integer receiverId);

    // 删除通知
    void deleteById(Integer id);
} 