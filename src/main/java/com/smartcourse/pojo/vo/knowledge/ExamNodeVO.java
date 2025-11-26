package com.smartcourse.pojo.vo.knowledge;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExamNodeVO {
    private Long id;
    private Double allScore;
    private Double getScore;
    private Double singleAmount;
    private Double multipleAmount;
    private Double fillBlankAmount;
    private Double shortAnswerAmount;
}
