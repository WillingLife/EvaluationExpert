package com.smartcourse.pojo.vo.exam.sql;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeShortQuestionSqlVO {
    private Long examScoreItemId;
    private String question;
    private String solution;
    private String answer;
    private BigDecimal score;
    private String criteria;
}
