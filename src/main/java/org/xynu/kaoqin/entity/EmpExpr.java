package org.xynu.kaoqin.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmpExpr {
    private Integer id;
    private Integer empId;
    private String company;
    private String job;
    private LocalDate begin;
    private LocalDate end;
}