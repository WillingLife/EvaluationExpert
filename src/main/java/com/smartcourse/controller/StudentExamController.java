package com.smartcourse.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartcourse.exception.SqlErrorException;
import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.dto.exam.StudentExamDTO;
import com.smartcourse.pojo.vo.exam.ExamScoreVO;
import com.smartcourse.pojo.vo.exam.StudentExamListVO;
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
@CrossOrigin
public class StudentExamController {

    private final StudentExamService studentExamService;

    @PostMapping("/exam-paper")
    public Result<StudentExamVO> getStudentExamPaper(@RequestBody StudentGetExamDTO studentGetExamDTO) {
        StudentExamVO studentExamPaper = studentExamService.getStudentExamPaper(studentGetExamDTO);
        return Result.success(studentExamPaper);
    }

    @PostMapping("/submit")
    public Result<String> submitStudentExam(@RequestBody StudentExamDTO studentExamDTO) {
        studentExamService.submit(studentExamDTO);
        return Result.success("success");
    }

    @GetMapping("/score/details")
    public Result<ExamScoreVO> getExamScore(@RequestBody StudentGetExamDTO studentGetExamDTO) {
        ExamScoreVO examScoreVO;
        try {
            examScoreVO = studentExamService.getScore(studentGetExamDTO);
        } catch (JsonProcessingException e) {
            throw new SqlErrorException("Json转换错误");
        }
        return Result.success(examScoreVO);
    }

    @GetMapping("/list")
    public Result<StudentExamListVO> getExamList(@RequestParam("course_id") Long courseId) {
        StudentExamListVO studentExamListVO = studentExamService.getList(courseId);
        return Result.success(studentExamListVO);
    }
}
