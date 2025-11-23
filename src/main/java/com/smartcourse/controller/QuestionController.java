package com.smartcourse.controller;

import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.vo.question.QuestionQueryESVO;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.QuestionAdvanceService;
import com.smartcourse.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping()
@CrossOrigin
public class QuestionController {
    private final QuestionAdvanceService questionAdvanceService;

    @PostMapping("/teacher/question/query-advance")
    Result<QuestionQueryESVO> queryAdvance(@RequestBody QuestionElasticSearchQueryDTO dto) {
        QuestionQueryESVO vo = questionAdvanceService.queryAdvance(dto);
        return Result.success(vo);
    }




}
