package com.smartcourse.pojo.vo.exam.question;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.enums.QuestionTypeEnum;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentExamSectionVO {
    private Long sectionId;
    private String title;
    private QuestionTypeEnum questionType;
    private Integer questionNumber;
    private String description;
    private List<StudentExamQuestionVO> questions;
}