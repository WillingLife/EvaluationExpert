package com.smartcourse.controller;

import com.smartcourse.pojo.dto.QuestionAddDTO;
import com.smartcourse.result.Result;
import com.smartcourse.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/question/add")
    public Result addQuestion(@RequestBody QuestionAddDTO questionAddDTO) {
        log.info("教师新增题目：{}", questionAddDTO);
        teacherService.addQuestion(questionAddDTO);

        return Result.success();
    }
}
