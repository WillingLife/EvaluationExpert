package com.smartcourse.pojo.vo.course;

import lombok.Data;

@Data
public class StudentCourseTaskVO {
    private Long id;
    private String name;
    private String teacherName;
    private Integer pending;
    private Integer submitted;
}
