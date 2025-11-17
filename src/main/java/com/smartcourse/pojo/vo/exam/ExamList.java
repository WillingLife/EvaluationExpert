package com.smartcourse.pojo.vo.exam;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExamList {
    private Long examId;
    private String examName;
    private String status;
    private BigDecimal score;
}
