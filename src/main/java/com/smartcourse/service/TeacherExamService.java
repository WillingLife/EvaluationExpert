package com.smartcourse.service;

import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.vo.exam.GradesVO;
import com.smartcourse.pojo.vo.exam.StudentExamVO;
import com.smartcourse.pojo.dto.exam.TeacherExamAiGenerateDTO;
import com.smartcourse.pojo.dto.exam.stream.AiStreamPayload;
import com.smartcourse.pojo.vo.exam.TeacherGetExamVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TeacherExamService {
    Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO);

    void publishExam(TeacherPublishExamDTO teacherPublishExamDTO);

    void submitGrade(TeacherGradeAssign teacherGradeDTO);

    TeacherViewAnswerVO viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO);

    TeacherGetExamVO getExamList(TeacherGetExamListDTO teacherGetExamListDTO);

    void deleteExam(TeacherDeleteExamDTO teacherDeleteExamDTO);

    Flux<AiStreamPayload> aiGenerateExam(TeacherExamAiGenerateDTO dto);

    StudentExamVO getStudentExamPaper(StudentGetExamDTO studentGetExamDTO);

    List<GradesVO> getGrades(Long examId, Long classId);
}
