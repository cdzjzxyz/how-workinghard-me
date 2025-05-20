package org.xynu.kaoqin.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Notification {
    private Integer id;
    private Integer senderId; // 发送者ID，系统通知可为空
    private Integer receiverId; // 接收者ID
    private Integer type; // 通知类型 (例如: 1请假审批, 2心声评论, 3新公告)
    private String content; // 通知内容
    private Integer relatedId; // 关联的业务ID (请假ID, 心声ID等), 可为空
    private Boolean isRead; // 是否已读 (false未读, true已读)
    private LocalDateTime createTime;
} 