package com.smartcourse.controller;


import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.vo.exam.StudentExamVO;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("student/exam")
public class StudentExamController {

    private final StudentExamService studentExamService;

    @GetMapping("/exam-paper")
    public Result<StudentExamVO> getStudentExamPaper(@RequestBody StudentGetExamDTO studentGetExamDTO) {
        StudentExamVO studentExamPaper = studentExamService.getStudentExamPaper(studentGetExamDTO);
        return Result.success(studentExamPaper);
    }

    @PostMapping("/submit")
    public Result submitStudentExam(@RequestBody StudentGetExamDTO studentGetExamDTO) {
        return Result.success();
    }

}
