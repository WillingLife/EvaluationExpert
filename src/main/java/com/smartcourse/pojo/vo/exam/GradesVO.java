package com.smartcourse.pojo.vo.exam;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradesVO {
    private Long studentId;
    private String studentName;
    private BigDecimal score;
}
