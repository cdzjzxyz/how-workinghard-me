package org.xynu.kaoqin.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmpVoiceComment {
    private Integer id;
    private Integer voiceId;
    private Integer empId;
    private String content;
    private LocalDateTime createTime;
}
