package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherGradeDTO {
    @JsonProperty("teacher_id")
    private Long teacherId;
    @JsonProperty("assignment_score_id")
    private Long assignmentScoreId;
    private Integer score;
    @JsonProperty("teacher_remark")
    private String teacherRemark;
}