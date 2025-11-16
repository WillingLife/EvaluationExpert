package com.smartcourse.controller;



import com.smartcourse.pojo.dto.TeacherSaveExamDTO;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("teacher/exam")
public class TeacherExamController {
    private final TeacherExamService teacherExamService;
    @PostMapping("make/save")
   public Result<Long> saveExam(TeacherSaveExamDTO teacherSaveExamDTO){
        Long exam_id = teacherExamService.saveExam(teacherSaveExamDTO);
        return Result.success(exam_id);
    }

}
