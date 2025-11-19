package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.BindParam;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherViewAnswerDTO {
    @BindParam("teacher_id")
    private Long teacherId;
    @BindParam("exam_id")
    private Long examId;
    @BindParam("student_id")
    private Long studentId;
}
