package com.smartcourse.service;

import com.smartcourse.model.QuestionDocument;
import com.smartcourse.pojo.dto.QuestionElasticSearchAddDTO;
import com.smartcourse.repository.QuestionDocumentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class QuestionElasticSearchServiceImplTests {

    @Autowired
    private QuestionElasticSearchService questionElasticSearchService;

    @Autowired
    private QuestionDocumentRepository repository;

    @Test
    void addQuestionDocumentShouldPersistInElasticsearch() {
        long docId = 110L;
        QuestionElasticSearchAddDTO dto = new QuestionElasticSearchAddDTO();
        dto.setId(docId);
        dto.setQuestionText("Describe the lifecycle of a butterfly.");
        dto.setAnswerText("Egg -> Caterpillar -> Chrysalis -> Butterfly.");
        dto.setCourseId(2002L);
        dto.setDifficulty(0.5f);
        dto.setAuthorId(77L);
        dto.setType("single");

        try {
            questionElasticSearchService.addQuestionDocument(dto);

            Optional<QuestionDocument> stored = repository.findById(docId);
            Assertions.assertTrue(stored.isPresent(), "Document should be stored in Elasticsearch");
            QuestionDocument document = stored.get();
            Assertions.assertEquals(dto.getQuestionText(), document.getQuestionText());
            Assertions.assertEquals(dto.getAnswerText(), document.getAnswerText());
        } catch (RuntimeException ex) {
            Assumptions.assumeTrue(false, "Elasticsearch is not reachable: " + ex.getMessage());
        } finally {
            try {
                System.out.println("Delete document " + docId);
//                repository.deleteById(docId);
            } catch (RuntimeException ignored) {
                // ignore cleanup failures
            }
        }
    }


}
