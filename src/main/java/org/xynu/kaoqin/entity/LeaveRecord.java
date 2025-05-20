package org.xynu.kaoqin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LeaveRecord {
    private Integer id;
    private Integer empId;
    private String name; // 员工姓名
    private String image; // 员工头像
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private Integer status; // 0待审批 1通过 2拒绝
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
