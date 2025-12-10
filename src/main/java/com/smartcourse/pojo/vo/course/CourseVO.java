package com.smartcourse.pojo.vo.course;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseVO {
    private Long id;
    private String name;
    private Long studentNumber;
    private Long examNumber;
    private Long taskNumber;
}
