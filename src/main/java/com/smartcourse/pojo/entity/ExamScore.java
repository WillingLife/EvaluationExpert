package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * 学生成绩实体，对应表 `exam_score`。
 * 说明：支持多次作答，按 attemptNo 区分；保留成绩快照 JSON。
 */
public class ExamScore {
    private Long id;                     // 主键ID
    private Long examId;                 // 考卷ID（逻辑外键）
    private Long studentId;              // 学生ID（逻辑外键）
    private Integer attemptNo;           // 作答序号（从1开始）
    private BigDecimal totalScore;       // 总分
    private String status;               // 状态：in_progress/submitted/graded/invalid
    private LocalDateTime startTime;     // 开始作答时间
    private LocalDateTime submitTime;    // 提交时间
    private LocalDateTime gradeTime;     // 评分完成时间
    private Integer durationSeconds;     // 实际用时（秒）
    private Long grader;                 // 评分人ID（逻辑外键）
    private String detailJson;           // 题目得分明细快照（JSON 字符串）
    private String note;                 // 备注
    private LocalDateTime createTime;    // 创建时间
    private LocalDateTime updateTime;    // 更新时间
    private Boolean deleted;             // 逻辑删除标记
}