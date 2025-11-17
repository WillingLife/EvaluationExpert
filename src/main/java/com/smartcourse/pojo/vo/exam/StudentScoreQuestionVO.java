package com.smartcourse.pojo.vo.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentScoreQuestionVO {
    private Long questionId;
    private Long fullScore;
    private Long studentScore;
    private String questionStem;
}
