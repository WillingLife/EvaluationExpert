package com.smartcourse.pojo.dto.exam;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.enums.QuestionTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherSaveExamSectionDTO {
    private Long sectionId;
    private Integer orderNo;
    private String title;
    private QuestionTypeEnum questionType;
    private Integer questionNumber;
    private String description;
    private BigDecimal choiceScore;
    private BigDecimal scoreNegativeScore;
    private String multipleStrategy;         // 多选题计分策略（本节级）
    @JsonRawValue
    private String multipleStrategyConf;
    private List<TeacherSaveExamQuestionDTO> questions;
}
