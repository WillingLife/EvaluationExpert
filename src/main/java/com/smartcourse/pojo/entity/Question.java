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
 */
public class Question {
    private Long id;                   // 主键ID
    private Integer type;              // 题型：1单选、2多选、3填空、4简答
    private Long courseId;             // 课程ID
    private String stem;               // 题干/材料（可含富文本）
    private String analysis;           // 解析/答案说明
    private Integer difficulty;        // 难度（1~5）
    private Long teacherId;            // 教师/出题人ID（逻辑外键）
    private Integer active;            // 启用状态
    private Integer deleted;           // 逻辑删除标记
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
}