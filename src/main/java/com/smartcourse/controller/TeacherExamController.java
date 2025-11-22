package com.smartcourse.controller;


import com.smartcourse.enums.QuestionTypeEnum;
import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.dto.exam.TeacherSaveExamQuestionDTO;
import com.smartcourse.pojo.dto.exam.TeacherSaveExamSectionDTO;
import com.smartcourse.pojo.vo.exam.TeacherGetExamVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("teacher/exam")
@CrossOrigin
public class TeacherExamController {
    private final TeacherExamService teacherExamService;

    @PostMapping("make/save")
    public Result<Long> saveExam(@RequestBody TeacherSaveExamDTO teacherSaveExamDTO) {
        for (TeacherSaveExamSectionDTO section : teacherSaveExamDTO.getSections()) {
            if (section.getQuestionType().equals(QuestionTypeEnum.SINGLE) || section.getQuestionType().equals(QuestionTypeEnum.MULTIPLE)) {
                for (TeacherSaveExamQuestionDTO question : section.getQuestions()) {
                    question.setScore(section.getChoiceScore());
                }
            }
        }
        Long exam_id = teacherExamService.saveExam(teacherSaveExamDTO);
        return Result.success(exam_id);
    }

    @PostMapping("publish")
    public Result<String> publishExam(@RequestBody TeacherPublishExamDTO teacherPublishExamDTO) {
        teacherExamService.publishExam(teacherPublishExamDTO);
        return Result.success("success");
    }

    @PostMapping("grade/submit")
    public Result<String> submitGrade(@RequestBody TeacherGradeAssign teacherGradeDTO) {
        teacherExamService.submitGrade(teacherGradeDTO);
        return Result.success("success");
    }

    @GetMapping("grade/student-answers")
    public Result<TeacherViewAnswerVO> viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO) {
        TeacherViewAnswerVO res = teacherExamService.viewStudentAnswers(teacherViewAnswerDTO);
        return Result.success(res);
    }


    @GetMapping("get-all")
    public Result<TeacherGetExamVO> getExamList(TeacherGetExamListDTO teacherGetExamListDTO) {
        TeacherGetExamVO res = teacherExamService.getExamList(teacherGetExamListDTO);
        return Result.success(res);
    }

    @PostMapping("delete")
    public Result<String> deleteExam(@RequestBody TeacherDeleteExamDTO teacherDeleteExamDTO) {
        teacherExamService.deleteExam(teacherDeleteExamDTO);
        return Result.success("success");
    }

}
