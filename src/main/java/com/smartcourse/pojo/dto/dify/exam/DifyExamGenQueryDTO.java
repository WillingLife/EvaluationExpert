package com.smartcourse.pojo.dto.dify.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyExamGenQueryDTO {
    private String userPrompt;
    private String context;
    private String selectedQuestionJson;
    private String knowledge;
}
