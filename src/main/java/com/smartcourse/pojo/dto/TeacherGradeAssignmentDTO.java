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
    private Long assignmentScoreId;
    private Integer score;
    private String teacherRemark;
}