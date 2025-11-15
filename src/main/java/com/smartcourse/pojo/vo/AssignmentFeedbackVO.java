package com.smartcourse.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentFeedbackVO {
    private Long assignmentScoreId;
    private List<DimensionItem> dimensions;
    private String aiRemark;
    private String teacherRemark;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionItem {
        private String name;
        private Integer score;
        private String remark;
    }
}