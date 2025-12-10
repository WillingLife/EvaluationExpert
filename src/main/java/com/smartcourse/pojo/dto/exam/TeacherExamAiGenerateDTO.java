package com.smartcourse.pojo.dto.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherExamAiGenerateDTO {
    private Long courseId;
    private String sessionId;
    private String prompt;
}
