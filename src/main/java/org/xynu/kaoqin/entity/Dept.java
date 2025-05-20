package org.xynu.kaoqin.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Dept {
    private Integer id;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 