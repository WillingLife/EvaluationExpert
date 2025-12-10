package com.smartcourse.pojo.dto.dify;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DifyGradeShortQuestionDTO {
    private String question;
    private String solution;
    private String answer;
    private BigDecimal score;
    private String criteria;
}
