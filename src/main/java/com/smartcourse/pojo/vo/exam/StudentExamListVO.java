package com.smartcourse.pojo.vo.exam;

import lombok.Data;

import java.util.List;

@Data
public class StudentExamListVO {
    private Long courseId;
    private List<ExamList> list;
}
