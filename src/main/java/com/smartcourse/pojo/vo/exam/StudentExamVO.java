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
public class StudentExamVO {
    /**
     * 考卷Id
     */
    private Long examId;

    private String examName;

    private String examNotice;

    private LocalDateTime startTime;

    private Integer durationMinutes;

    private List<StudentExamSectionVO> sections;

    private Long courseId;
    private String description;
    private BigDecimal totalScore;
    private BigDecimal passScore;
}
