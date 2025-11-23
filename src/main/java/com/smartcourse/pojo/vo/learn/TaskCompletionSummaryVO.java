package com.smartcourse.pojo.vo.learn;

import lombok.Data;

@Data
public class TaskCompletionSummaryVO {
    private Integer taskId;
    private String taskTitle;
    private Integer submittedCount;
    private Double averageScore;
}
