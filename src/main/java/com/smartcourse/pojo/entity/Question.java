package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * 题目基表实体，对应表 question
 * 说明：承载所有题目公共属性，类型与子表一一对应。
 */
public class Question {
    private Long id;                   // 主键ID
    private String type;               // 题型：single/multiple/fill_blank/short_answer/group
    private Long courseId;             // 课程ID（逻辑外键）
    private String stem;               // 题干/材料（可含富文本）
    private String analysis;           // 解析/答案说明
    private Integer difficulty;        // 难度（1~5）
    private Long authorId;             // 出题人ID（逻辑外键）
    private Boolean active;            // 启用状态
    private Boolean deleted;           // 逻辑删除标记
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}