package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherAssignmentListDTO {
    @JsonProperty("teacher_id")
    private Long teacherId;
    @JsonProperty("course_id")
    private Long courseId;
}