package com.smartcourse.pojo.vo.learn;

import lombok.Data;

@Data
public class StudentCoursePerformanceVO {
    private Long courseId;
    private String courseName;
    private Double completionRate;
    private Double averageScore;
}