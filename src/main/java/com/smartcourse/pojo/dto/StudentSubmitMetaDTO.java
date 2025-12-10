package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class StudentSubmitMetaDTO {
    @JsonProperty("student_id")
    private Long studentId;
    @JsonProperty("assignment_id")
    private Long assignmentId;
    @JsonProperty("submission_time")
    private OffsetDateTime submissionTime;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("file_type")
    private String fileType;
}