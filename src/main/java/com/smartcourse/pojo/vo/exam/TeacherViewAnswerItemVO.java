package com.smartcourse.pojo.vo.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherViewAnswerItemVO {
    private Long examItemId;
    private BigDecimal fullScore;
    private BigDecimal studentScore;
    private BigDecimal aiScore;
    private String studentAnswer;
    private String answer;
    private String criteria;
}
