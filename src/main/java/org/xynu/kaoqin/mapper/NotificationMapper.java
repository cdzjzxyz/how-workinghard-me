package org.xynu.kaoqin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xynu.kaoqin.entity.Notification;

@Mapper
public interface NotificationMapper {
    // 插入通知
    int insert(Notification notification);

    // 根据接收者ID查询通知列表
    List<Notification> selectByReceiverId(@Param("receiverId") Integer receiverId);

    // 将通知标记为已读
    int updateIsReadById(@Param("id") Integer id, @Param("isRead") Boolean isRead);

    // 根据接收者ID统计未读通知数量
    int countUnreadByReceiverId(@Param("receiverId") Integer receiverId);

    // 删除通知 (可选)
    int deleteById(@Param("id") Integer id);
} 