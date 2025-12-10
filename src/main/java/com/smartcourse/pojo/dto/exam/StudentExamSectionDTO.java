package com.smartcourse.pojo.dto.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.enums.QuestionTypeEnum;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentExamSectionDTO {
    private Long sectionId;
    private QuestionTypeEnum questionType;
    private List<StudentExamQuestionDTO> questions;
}
