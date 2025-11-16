package com.smartcourse.controller;



import com.smartcourse.pojo.dto.TeacherGradeDTO;
import com.smartcourse.pojo.dto.TeacherPublishExamDTO;
import com.smartcourse.pojo.dto.TeacherSaveExamDTO;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("teacher/exam")
public class TeacherExamController {
    private final TeacherExamService teacherExamService;
    @PostMapping("make/save")
   public Result<Long> saveExam(@RequestBody TeacherSaveExamDTO teacherSaveExamDTO){
        Long exam_id = teacherExamService.saveExam(teacherSaveExamDTO);
        return Result.success(exam_id);
    }

    @PostMapping("publish")
    public Result<String> publishExam(@RequestBody TeacherPublishExamDTO teacherPublishExamDTO){
        teacherExamService.publishExam(teacherPublishExamDTO);
        return Result.success("success");
    }

    @PostMapping("grade/submit")
    public Result<String> submitGrade(@RequestBody TeacherGradeDTO teacherGradeDTO){
        teacherExamService.submitGrade(teacherGradeDTO);
        return Result.success("success");
    }



}
