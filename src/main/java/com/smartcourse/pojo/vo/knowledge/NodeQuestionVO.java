package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NodeQuestionVO {
    private Long questionId;
    private BigDecimal totalScore;
    private BigDecimal score;
    private String questionType;
}
