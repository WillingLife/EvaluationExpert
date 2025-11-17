package com.smartcourse.pojo.vo.exam.question;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentExamFillBlankQuestionVO extends StudentExamQuestionVO{
    private Integer blankCount;
}
