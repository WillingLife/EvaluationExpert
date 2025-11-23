package com.smartcourse.service;

import com.smartcourse.infra.rabbitmq.TaskProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DifyServiceTests {
    @Autowired
    private DifyService difyService;
    @Autowired
    private TaskProducer taskProducer;

    @Test
    public void testDifyService() {
        difyService.mappingKnowledge(1L,1L,"项目的最显著特征是______和独特性，这意味着每个项目都有明确的起点 and 终点。");
    }

    @Test
    public void testDifyService2() throws InterruptedException {
        taskProducer.publishMappingKnowledgeTask(2L,1L,"项目的最显著特征是______和独特性，这意味着每个项目都有明确的起点 and 终点。");
        Thread.sleep(1000000);
    }
}
