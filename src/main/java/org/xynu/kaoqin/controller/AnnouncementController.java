package org.xynu.kaoqin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xynu.kaoqin.common.Result;
import org.xynu.kaoqin.entity.Announcement;
import org.xynu.kaoqin.entity.Emp;
import org.xynu.kaoqin.entity.Notification;
import org.xynu.kaoqin.mapper.EmpMapper;
import org.xynu.kaoqin.service.AnnouncementService;
import org.xynu.kaoqin.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmpMapper empMapper;

    // 添加公告 (通常只有admin有权限，这里简化，实际应用中需加权限校验)
    @PostMapping
    public Result<?> add(@RequestBody Announcement announcement) {
        announcementService.addAnnouncement(announcement);

        // 创建系统通知给所有员工
        List<Emp> allEmps = empMapper.selectAll();
        for (Emp emp : allEmps) {
             Notification notification = new Notification();
             notification.setSenderId(null);
             notification.setReceiverId(emp.getId());
             notification.setType(3);
             notification.setContent("发布了新公告：" + announcement.getTitle());
             notification.setRelatedId(announcement.getId());
             notification.setIsRead(false);
             notificationService.createNotification(notification);
        }

        return Result.success(null);
    }

    // 获取公告列表 (可能需要分页)
    @GetMapping
    public Result<List<Announcement>> list() {
        List<Announcement> list = announcementService.getAnnouncements();
        return Result.success(list);
    }

    // 获取最新公告 (供首页调用)
    @GetMapping("/latest")
    public Result<Announcement> getLatest() {
        Announcement latestAnnouncement = announcementService.getLatestAnnouncement();
        return Result.success(latestAnnouncement);
    }

    // 根据ID获取公告
    @GetMapping("/{id}")
    public Result<Announcement> getById(@PathVariable Integer id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        return Result.success(announcement);
    }

    // 更新公告 (通常只有admin有权限)
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementService.updateAnnouncement(announcement);
        return Result.success(null);
    }

    // 删除公告 (通常只有admin有权限)
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        announcementService.deleteAnnouncement(id);
        return Result.success(null);
    }
}
