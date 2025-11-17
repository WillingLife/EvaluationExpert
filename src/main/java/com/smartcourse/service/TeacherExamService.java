package com.smartcourse.service;

import com.smartcourse.pojo.dto.TeacherGradeDTO;
import com.smartcourse.pojo.dto.TeacherPublishExamDTO;
import com.smartcourse.pojo.dto.TeacherSaveExamDTO;
import com.smartcourse.pojo.dto.TeacherViewAnswerDTO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;

public interface TeacherExamService {
    Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO);

    void publishExam(TeacherPublishExamDTO teacherPublishExamDTO);

    void submitGrade(TeacherGradeDTO teacherGradeDTO);

    TeacherViewAnswerVO viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO);
}
