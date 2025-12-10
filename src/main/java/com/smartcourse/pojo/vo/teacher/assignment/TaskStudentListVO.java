package com.smartcourse.pojo.vo.teacher.assignment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskStudentListVO {
    private Long id;
    private String studentName;
    private String className;
    private String status;
    private BigDecimal score;
}
