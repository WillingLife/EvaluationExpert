package com.smartcourse.pojo.vo.learn;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskVO {
    private Integer taskId;
    private String taskTitle;
    private String taskType;
    private Long courseId;
    private String courseName;
    private LocalDateTime deadline;
    private Integer maxScore;
    private String status;
    private Integer score;
    private String isOverdue;
    private Integer daysUntilDeadline;
}
