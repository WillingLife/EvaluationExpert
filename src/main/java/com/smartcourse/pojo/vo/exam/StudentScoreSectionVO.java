package com.smartcourse.pojo.vo.exam;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.enums.QuestionTypeEnum;
import com.smartcourse.pojo.vo.exam.question.StudentExamQuestionVO;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentScoreSectionVO {
    private Long sectionId;
    private String title;
    private String questionType;
    private Integer questionNumber;
    private List<StudentScoreQuestionVO> questions;
}