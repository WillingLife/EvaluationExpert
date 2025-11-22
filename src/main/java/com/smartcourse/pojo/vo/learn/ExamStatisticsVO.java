package com.smartcourse.pojo.vo.learn;

import lombok.Data;

@Data
public class ExamStatisticsVO {
    private Integer totalExams;
    private Integer completedExams;
    private Integer passedExams;
    private Integer failedExams;
    private Double averageScore;
}
