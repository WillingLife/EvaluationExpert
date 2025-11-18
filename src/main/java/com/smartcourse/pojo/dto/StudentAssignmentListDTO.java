package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAssignmentListDTO {
    @JsonProperty("student_id")
    private Long studentId;
    @JsonProperty("course_id")
    private Long courseId;
}