package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.BindParam;

@Data
@Builder
@AllArgsConstructor
public class StudentAssignmentListDTO {
    @JsonProperty("course_id")
    @BindParam("course_id")
    private Long courseId;

    @JsonProperty("student_id")
    @BindParam("student_id")
    private Long studentId;
}