package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * 题目与知识点关联实体，对应表 `question_knowledge`。
 * 说明：用于按权重关联题目到知识点，支持统计与推荐。
 */
public class QuestionKnowledge {
    private Long questionId;        // 题目ID
    private Long knowledgeNodeId;   // 知识点ID
    private BigDecimal weight;      // 关联权重（0-1区间或百分比）
}