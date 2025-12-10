package com.smartcourse.pojo.dto.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.enums.QuestionTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherSaveExamQuestionDTO {
    private Long examItemId;
    private Long questionId;
    private BigDecimal score;
}
