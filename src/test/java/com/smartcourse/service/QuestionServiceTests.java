package com.smartcourse.service;

import com.smartcourse.pojo.vo.QuestionQueryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class QuestionServiceTests {
    @Autowired
    private QuestionService questionService;

    @Test
    void getQuestionById() {
        List<QuestionQueryVO> batch = questionService.getBatch(Arrays.asList(94L, 117L, 413L));
        System.out.println(batch);
    }
}
