package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
public class AssignmentAddDTO {
    @JsonProperty("teacher_id")
    private Long teacherId;
    @JsonProperty("course_id")
    private Long courseId;
    private String name;
    private String description;
    @JsonProperty("submit_limit")
    private Integer submitLimit;
    private LocalDateTime deadline;
}