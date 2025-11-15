package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class AssignmentUpdateDTO {
    @JsonProperty("assignment_id")
    private Long assignmentId;
    private String name;
    private String description;
    @JsonProperty("submit_limit")
    private Integer submitLimit;
    private OffsetDateTime deadline;
}