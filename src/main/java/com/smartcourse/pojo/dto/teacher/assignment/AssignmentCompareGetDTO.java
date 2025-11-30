package com.smartcourse.pojo.dto.teacher.assignment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignmentCompareGetDTO {
    private Long assignmentId;
    private Long leftStudentId;
    private Long rightStudentId;
}
