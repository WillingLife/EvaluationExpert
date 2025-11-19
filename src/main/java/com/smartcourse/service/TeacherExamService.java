package com.smartcourse.service;

import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.vo.exam.TeacherGetExamVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;

public interface TeacherExamService {
    Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO);

    void publishExam(TeacherPublishExamDTO teacherPublishExamDTO);

    void submitGrade(TeacherGradeDTO teacherGradeDTO);

    TeacherViewAnswerVO viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO);

    TeacherGetExamVO getExamList(TeacherGetExamListDTO teacherGetExamListDTO);

    void deleteExam(TeacherDeleteExamDTO teacherDeleteExamDTO);
}
