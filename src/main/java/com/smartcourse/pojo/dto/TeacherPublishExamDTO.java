package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherPublishExamDTO {
    private Long teacher_id;
    private Long course_id;
    private Long exam_id;
    private LocalDateTime startTime;
    private Integer durationMinutes;

}
