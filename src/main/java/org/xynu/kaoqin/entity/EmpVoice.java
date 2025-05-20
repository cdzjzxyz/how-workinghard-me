package org.xynu.kaoqin.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmpVoice {
    private Integer id;
    private Integer empId;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
