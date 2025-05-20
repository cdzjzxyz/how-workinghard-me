package org.xynu.kaoqin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Emp {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private Integer gender;
    private String phone;
    private Integer job; // 职位
    private Integer salary;
    private String image;
    private LocalDate entryDate; // 入职日期
    private Integer deptId;
    private String deptName; // 关联部门名
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<EmpExpr> exprList; // 工作经历
    private Integer exprType;
}