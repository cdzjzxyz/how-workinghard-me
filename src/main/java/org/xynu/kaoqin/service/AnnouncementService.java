package org.xynu.kaoqin.service;

import org.xynu.kaoqin.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    void addAnnouncement(Announcement announcement);
    List<Announcement> getAnnouncements();
    Announcement getAnnouncementById(Integer id);
    void updateAnnouncement(Announcement announcement);
    void deleteAnnouncement(Integer id);
    Announcement getLatestAnnouncement();
}
