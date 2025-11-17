package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentGetExamDTO {
    /**
     * 学生ID
     */
    private Long studentId;
    /**
     * 考卷Id
     */
    private Long examId;
}
