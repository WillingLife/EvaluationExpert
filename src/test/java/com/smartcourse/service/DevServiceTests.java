package com.smartcourse.service;

import com.smartcourse.model.QuestionKnowledgeDocument;
import com.smartcourse.pojo.entity.KnowledgeNode;
import com.smartcourse.repository.elastic.QuestionKnowledgeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class DevServiceTests {
    @Autowired
    private DevService devService;
    @Autowired
    private QuestionKnowledgeRepository questionKnowledgeRepository;

    @Test
    void testDevService(){
        devService.updateQuestionKnowledge(1L,231L,481L);
        Optional<QuestionKnowledgeDocument> id = questionKnowledgeRepository.findById(1L);
    }


}
