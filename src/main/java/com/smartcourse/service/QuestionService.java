package com.smartcourse.service;

import com.smartcourse.pojo.dto.QuestionAddDTO;
import com.smartcourse.pojo.dto.QuestionQueryDTO;
import com.smartcourse.pojo.dto.QuestionUpdateDTO;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.result.PageResult;

public interface QuestionService {
    /**
     * 新增题目
     * @param questionAddDTO 题目信息
     */
    void addQuestion(QuestionAddDTO questionAddDTO);

    /**
     * 分页查询题目数据
     * @param questionQueryDTO 查询条件
     * @return 题目数据
     */
    PageResult<QuestionQueryVO> page(QuestionQueryDTO questionQueryDTO);

    QuestionQueryVO get(Long id);

    void update(QuestionUpdateDTO questionUpdateDTO);
}
