package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherViewAnswerDTO {
    private Long teacherId;
    private Long examId;
    private Long studentId;
}
