package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRemark {
    private Long id;
    private Long assignmentScoreId;
    private String aiRemark;
    private String teacherRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}