package com.smartcourse.controller;


import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.vo.exam.StudentExamVO;
import com.smartcourse.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("student/exam")
public class StudentExamController {

    private final StudentExamService studentExamService;

    @GetMapping("/exam-paper")
    public StudentExamVO getStudentExamPaper(@RequestBody StudentGetExamDTO studentGetExamDTO) {

        return studentExamService.getStudentExamPaper(studentGetExamDTO);
    }

}
