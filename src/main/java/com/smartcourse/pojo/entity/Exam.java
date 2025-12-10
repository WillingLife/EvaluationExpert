package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * 考卷实体，对应表exam
 */
public class Exam {
    private Long id;                        // 主键ID
    private String name;                    // 考卷名称
    private String description;             // 考卷描述
    private String notice;                  // 注意事项（可为富文本）
    private Long courseId;                  // 课程ID（逻辑外键）
    private BigDecimal totalScore;          // 总分
    private Integer durationMinutes;        // 考试时长（分钟）
    private LocalDateTime startTime;        // 考试开始时间
    private BigDecimal passScore;           // 及格分，可选
    private Boolean shuffleQuestions;       // 是否乱序题目
    private Boolean shuffleOptions;         // 是否乱序选项
    private String status;                  // 状态：draft/published/archived
    private Integer version;                // 版本号
    private Long creator;                   // 创建者ID（逻辑外键）
    private LocalDateTime createTime;       // 创建时间
    private LocalDateTime updateTime;       // 更新时间
    private Boolean deleted;                // 逻辑删除标记
    /**
     * 该考卷对应的班级
     */
    private List<Long> classIds;
    private List<ExamSection> sections;

    public void batchUpdateExamIdIntoSections(){
        for (ExamSection section : sections) {
            section.setExamId(this.id);
        }
    }

    public void batchUpdateSectionIdIntoExamItems(){
        for (ExamSection section : sections) {
            section.batchUpdateExamIdInSections();
        }
    }

    public boolean hasExistingSections() {
        if (sections == null) {
            return false;
        }
        for (ExamSection section : sections) {
            if (section != null && section.getId() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasExistingExamItems() {
        if (sections == null) {
            return false;
        }
        for (ExamSection section : sections) {
            if (section == null || section.getExamItems() == null) {
                continue;
            }
            for (ExamItem item : section.getExamItems()) {
                if (item != null && item.getId() != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
