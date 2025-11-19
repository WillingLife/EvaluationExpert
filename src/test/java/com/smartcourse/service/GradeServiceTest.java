package com.smartcourse.service;

import com.smartcourse.infra.rabbitmq.TaskProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GradeServiceTest {
    @Autowired
    private GradeService gradeService;
    @Autowired
    private TaskProducer  taskProducer;

    @Test
    void getAllGrades() throws InterruptedException {
        taskProducer.publishGradeShortQuestionTask(4L,30L);
        System.out.println("消息已发送");
        Thread.sleep(100000);
    }
}
