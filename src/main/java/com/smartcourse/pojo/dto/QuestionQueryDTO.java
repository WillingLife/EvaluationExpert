package com.smartcourse.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionQueryDTO {
    private Long courseId;             // 课程ID（逻辑外键）
    private String stem;               // 题干关键词
    private Integer difficulty;        // 难度（1~5）
    private Integer type;
    private Integer page;
    private Integer pageSize;
}
