package org.xynu.kaoqin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Attendance {
    private Integer id;
    private Integer empId;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer status; // 1正常 2迟到 3早退 4缺卡 5请假
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer performanceScore;
}
