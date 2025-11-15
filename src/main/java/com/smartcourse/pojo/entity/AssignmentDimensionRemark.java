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
public class AssignmentDimensionRemark {
    private Long id;
    private Long assignmentRemarkId;
    private Long dimensionId;
    private Long dimensionGroupId;
    private BigDecimal score;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;
}