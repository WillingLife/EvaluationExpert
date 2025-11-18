package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignmentDeleteDTO {
    @JsonProperty("teacher_id")
    private Long teacherId;
    @JsonProperty("course_id")
    private Long courseId;
    @JsonProperty("assignment_id")
    private Long assignmentId;
}