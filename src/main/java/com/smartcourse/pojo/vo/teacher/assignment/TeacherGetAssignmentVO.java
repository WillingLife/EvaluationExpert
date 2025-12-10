package com.smartcourse.pojo.vo.teacher.assignment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherGetAssignmentVO {
    private String name;
    private String description;
    private String assignmentUrl;
}
