package com.smartcourse.service;

import com.smartcourse.pojo.dto.QuestionAddDTO;

public interface TeacherService {
    /**
     * 新增题目
     * @param questionAddDTO 题目信息
     */
    void addQuestion(QuestionAddDTO questionAddDTO);
}
