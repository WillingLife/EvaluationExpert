package com.smartcourse.controller;

import com.smartcourse.pojo.dto.QuestionAddDTO;
import com.smartcourse.pojo.dto.QuestionQueryDTO;
import com.smartcourse.pojo.dto.QuestionUpdateDTO;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.result.PageResult;
import com.smartcourse.result.Result;
import com.smartcourse.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final QuestionService questionService;

    @PostMapping("/question/add")
    public Result addQuestion(@RequestBody QuestionAddDTO questionAddDTO) {
        log.info("教师新增题目：{}", questionAddDTO);
        questionService.addQuestion(questionAddDTO);

        return Result.success();
    }

    /**
     * 分页查询题目数据
     * @param questionQueryDTO 查询条件
     * @return 题目数据
     */
    @GetMapping("/question/query")
    public Result page(QuestionQueryDTO questionQueryDTO) {
        log.info("教师查看题目：{}", questionQueryDTO);
        PageResult<QuestionQueryVO> pageResult = questionService.page(questionQueryDTO);

        return Result.success(pageResult);
    }

    @GetMapping("/question/{id}")
    public Result get(@PathVariable Long id) {
        log.info("教师查看题目详情：{}", id);
        QuestionQueryVO vo = questionService.get(id);

        return Result.success(vo);
    }

    @PutMapping("/question/update")
    public Result updateQuestion(@RequestBody QuestionUpdateDTO questionUpdateDTO) {
        log.info("教师修改题目：{}", questionUpdateDTO);
        questionService.update(questionUpdateDTO);

        return Result.success();
    }
}
