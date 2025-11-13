package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * 组合题关联实体，对应表 `question_group`。
 * 说明：用于维护父子题目关系（如阅读理解）。
 */
public class QuestionGroup {
    private Long id;                // 主键ID
    private Long groupQuestionId;   // 父题目ID（组合题主体）
    private Long childQuestionId;   // 子题目ID（从属题）
}