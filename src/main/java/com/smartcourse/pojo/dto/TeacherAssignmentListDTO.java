package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.BindParam;

@Data
@Builder
@AllArgsConstructor
public class TeacherAssignmentListDTO {
    @JsonProperty("teacher_id")
    @BindParam("teacher_id")
    private Long teacherId;

    @JsonProperty("course_id")
    @BindParam("course_id")
    private Long courseId;
}