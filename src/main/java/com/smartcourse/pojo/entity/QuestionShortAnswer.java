package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 简答题答案实体，对应表 question_short_answer
 * 说明：每题一行，存储参考答案与评分参考。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionShortAnswer {
    private Long questionId;   // 题目ID（主键，逻辑外键），须为简答题（type=4）
    private String answer;     // 参考答案（TEXT）
    private String criteria;   // 评分要点/参考（TEXT，可选）
}