package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.pojo.dto.exam.TeacherSaveExamSectionDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherSaveExamDTO {
    private Long examId;
    private Long courseId;
    private Long teacherId;
    private String description;
    private String examName;
    private String examNotice;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private BigDecimal totalScore;
    private BigDecimal passeScore;
    private Boolean shuffleQuestions;
    private Boolean shuffleOptions;
    private Integer version;
    private List<TeacherSaveExamSectionDTO> sections;
}
