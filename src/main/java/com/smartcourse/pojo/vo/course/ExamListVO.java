package com.smartcourse.pojo.vo.course;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamListVO {
    private Long classId;
    private String name;
    private String status;
    private Long examId;
    private String examName;
}
