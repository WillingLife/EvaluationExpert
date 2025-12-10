package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 填空题可接受答案实体，对应表 question_fill_blank
 * 说明：每行表示某题目某空位的一条可接受答案。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionFillBlank {
    private Long id;             // 主键ID
    private Long questionId;     // 题目ID（逻辑外键），须为填空题（type=3）
    private Integer blankIndex;  // 空位索引（从1开始）
    private String answer;       // 可接受答案
}