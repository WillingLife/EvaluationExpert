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
 * 选择题选项实体，对应表question_option
 * 说明：服务单/多选题，支持顺序与正确标记。
 */
public class QuestionOption {
    private Long id;             // 主键ID
    private Long questionId;     // 题目ID（逻辑外键）
    private String content;      // 选项内容（可含富文本/图片引用）
    private Integer correct;     // 是否为正确选项
    private Integer sortOrder;   // 显示顺序
}