package com.smartcourse.pojo.vo.learn;

import lombok.Data;

@Data
public class KnowledgePointPerformanceVO {
    private Long knowledgePointId;
    private String knowledgePointName;
    private String masteryLevel;
    private Double averageScore;
}
