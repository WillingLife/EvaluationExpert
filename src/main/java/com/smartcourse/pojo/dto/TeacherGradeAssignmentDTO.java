package com.smartcourse.pojo.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherGradeAssignmentDTO {
    private Long teacherId;
    private Long assignmentId;
    private Long studentId;
    private Integer score;
    private String comment;
}