package org.xynu.kaoqin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xynu.kaoqin.entity.Notification;
import org.xynu.kaoqin.mapper.NotificationMapper;
import org.xynu.kaoqin.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void createNotification(Notification notification) {
        // 可以在这里添加一些默认值或业务逻辑，例如设置isRead为false
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        notificationMapper.insert(notification);
    }

    @Override
    public List<Notification> getNotificationsByReceiverId(Integer receiverId) {
        return notificationMapper.selectByReceiverId(receiverId);
    }

    @Override
    public void markAsRead(Integer notificationId) {
        notificationMapper.updateIsReadById(notificationId, true);
    }

    @Override
    public int countUnreadNotifications(Integer receiverId) {
        return notificationMapper.countUnreadByReceiverId(receiverId);
    }

    @Override
    public void deleteById(Integer id) {
        notificationMapper.deleteById(id);
    }
} 