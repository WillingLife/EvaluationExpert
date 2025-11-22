package com.smartcourse.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DevServiceTests {
    @Autowired
    private DevService devService;
    @Test
    void testDevService(){
        devService.updateQuestionKnowledge(1L,231L,481L);
    }

}
