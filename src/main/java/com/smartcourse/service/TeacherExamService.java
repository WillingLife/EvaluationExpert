package com.smartcourse.service;

import com.smartcourse.pojo.dto.TeacherGradeDTO;
import com.smartcourse.pojo.dto.TeacherPublishExamDTO;
import com.smartcourse.pojo.dto.TeacherSaveExamDTO;

public interface TeacherExamService {
    Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO);

    void publishExam(TeacherPublishExamDTO teacherPublishExamDTO);

    void submitGrade(TeacherGradeDTO teacherGradeDTO);
}
