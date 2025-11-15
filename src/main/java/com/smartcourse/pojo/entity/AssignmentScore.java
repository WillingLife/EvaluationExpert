package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentScore {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private BigDecimal score;
    private String status;
    private Integer submitNo;
    private String submitFileUrl;
    private Long grader;
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}