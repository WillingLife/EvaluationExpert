package com.smartcourse.pojo.dto.exam;

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
 * 学生题目成绩明细实体，对应表 exam_score_item
 * 说明：一次作答中的单题成绩与答案记录。
 */
public class ExamScoreItemDTO {
    private Long id;                      // 主键ID
    private Long scoreId;                 // 成绩ID（逻辑外键）
    private Long examItemId;              // 考卷题目明细ID（逻辑外键）
    private BigDecimal score;             // 该题得分
    private String answer;                // 学生作答（JSON 字符串）
    private String autoJudgeDetailJson;   // 自动判分过程/依据（JSON 字符串）
    private String remark;                // 评语/扣分说明
    private LocalDateTime gradeTime;      // 题目评分完成时间
    private LocalDateTime createTime;     // 创建时间
    private LocalDateTime updateTime;     // 更新时间
    private Boolean deleted;              // 逻辑删除标记
    private Long questionId;
}