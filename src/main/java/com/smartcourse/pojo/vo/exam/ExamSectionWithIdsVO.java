package com.smartcourse.pojo.vo.exam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSectionWithIdsVO {
    private Long id;                         // 主键ID
    private Long examId;                     // 考卷ID（逻辑外键）
    private String title;                    // 分节/大题标题
    private String questionType;             // 题型编码：single/multiple/fill_blank/short_answer 等
    private String description;              // 分节描述
    private BigDecimal choiceScore;          // 选择题每题分值（当本节为选择题时）
    private Integer orderNo;                 // 显示顺序（从1递增）
    private String note;                     // 备注
    private BigDecimal choiceNegativeScore;  // 选择题错题扣分，默认0
    private String multipleStrategy;         // 多选题计分策略（本节级）
    private String multipleStrategyConf;     // 计分策略参数（JSON 字符串）
    private Long creator;                    // 创建者ID（逻辑外键）
    private LocalDateTime createTime;        // 创建时间
    private LocalDateTime updateTime;        // 更新时间
    private Boolean deleted;                 // 逻辑删除标记
    List<Long> questionIds;
}
