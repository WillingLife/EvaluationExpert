package com.smartcourse.pojo.dto.teacher.assignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.BindParam;

@Data
@AllArgsConstructor
public class TeacherGetAssignmentDTO {
    @BindParam("student_id")
    private Long studentId;
    @BindParam("assignment_id")
    private Long assignmentId;
}
