package com.smartcourse.pojo.vo.course;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExamStudentVO {
    private Long id;
    private String name;
    private String status;
    private BigDecimal score;
}
