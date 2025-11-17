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
 * 考卷题目明细实体，对应表 exam_item
 * 说明：静态选题，包含节内排序与题目级计分策略覆盖。
 */
public class ExamItem {
    private Long id;                  // 主键ID
    private Long sectionId;           // 分节ID（逻辑外键）
    private Long questionId;          // 题目ID（逻辑外键）
    private BigDecimal score;         // 题目分值（选择题可为空，使用分节分值）
    private Integer orderNo;          // 题目在分节中的顺序
}