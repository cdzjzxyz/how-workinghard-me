package org.xynu.kaoqin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xynu.kaoqin.entity.Announcement;

@Mapper
public interface AnnouncementMapper {
    int insert(Announcement announcement); // 添加公告
    List<Announcement> selectList(); // 查询所有公告 (管理列表可能需要分页，这里先简单返回全部)
    Announcement selectById(@Param("id") Integer id); // 根据ID查询公告
    int updateById(Announcement announcement); // 更新公告
    int deleteById(@Param("id") Integer id); // 删除公告
    Announcement selectLatest(); // 查询最新公告
}
