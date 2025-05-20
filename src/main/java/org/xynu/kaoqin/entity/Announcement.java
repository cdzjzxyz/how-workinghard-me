package org.xynu.kaoqin.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
