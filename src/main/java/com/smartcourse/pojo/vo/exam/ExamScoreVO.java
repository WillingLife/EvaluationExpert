package com.smartcourse.pojo.vo.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.pojo.vo.exam.question.StudentExamSectionVO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExamScoreVO {
    private Long id;
    private Long examId;                     // 主键ID
    private Long studentId;              // 学生ID（逻辑外键）
    private String examName;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private LocalDateTime gradeTime;
    private BigDecimal totalScore;       // 总分
    private List<StudentScoreSectionVO> sections;
}
