package com.smartcourse.pojo.dto.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.pojo.vo.exam.question.StudentExamSectionVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentExamDTO {
    private Long examId;

    private Long studentId;

    private LocalDateTime startTime;

    private LocalDateTime submitTime;

    private List<StudentExamSectionDTO> sections;
}
