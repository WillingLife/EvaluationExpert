package com.smartcourse.pojo.vo.exam.items;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeacherGetExamItemVO {
    private Long examId;
    private String examName;
    private String description;
    private BigDecimal totalScore;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<TeacherExamClassItemVO> classes;
}
