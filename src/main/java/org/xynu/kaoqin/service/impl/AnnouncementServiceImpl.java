package org.xynu.kaoqin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xynu.kaoqin.entity.Announcement;
import org.xynu.kaoqin.mapper.AnnouncementMapper;
import org.xynu.kaoqin.service.AnnouncementService;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public void addAnnouncement(Announcement announcement) {
        announcementMapper.insert(announcement);
    }

    @Override
    public List<Announcement> getAnnouncements() {
        return announcementMapper.selectList();
    }

    @Override
    public Announcement getAnnouncementById(Integer id) {
        return announcementMapper.selectById(id);
    }

    @Override
    public void updateAnnouncement(Announcement announcement) {
        announcementMapper.updateById(announcement);
    }

    @Override
    public void deleteAnnouncement(Integer id) {
        announcementMapper.deleteById(id);
    }

    @Override
    public Announcement getLatestAnnouncement() {
        return announcementMapper.selectLatest();
    }
}
