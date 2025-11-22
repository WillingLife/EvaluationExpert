package com.smartcourse.pojo.vo.course;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentCourseVO {
    private int id;
    private String name;
    private String teacherName;
    private Integer examNumber;
    private Integer ongoing;
    private Integer upcoming;
    private Integer reviewing;
}
