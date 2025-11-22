package com.smartcourse.service;

import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.vo.exam.TeacherGetExamVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface TeacherExamService {
    Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO);

    void publishExam(TeacherPublishExamDTO teacherPublishExamDTO);

    void submitGrade(TeacherGradeAssign teacherGradeDTO);

    TeacherViewAnswerVO viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO);

    TeacherGetExamVO getExamList(TeacherGetExamListDTO teacherGetExamListDTO);

    void deleteExam(TeacherDeleteExamDTO teacherDeleteExamDTO);

    Flux<ChatResponse> aiGenerateExam();
}
