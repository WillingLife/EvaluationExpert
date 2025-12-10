package com.smartcourse.pojo.vo.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExamVO {
    private Long examId;
    private String examName;
    private String status;
    private String totalScore;
    private String description;
    private String durationMinutes;
    private LocalDateTime startTime;
}
