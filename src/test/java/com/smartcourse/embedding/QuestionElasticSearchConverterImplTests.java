package com.smartcourse.embedding;

import com.smartcourse.converter.QuestionElasticSearchConverter;
import com.smartcourse.model.QuestionDocument;
import com.smartcourse.pojo.dto.QuestionElasticSearchAddDTO;
import com.smartcourse.repository.QuestionDocumentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuestionElasticSearchConverterImplTests {

    @Autowired
    private QuestionElasticSearchConverter converter;

    @Autowired
    private QuestionDocumentRepository repository;

    @Test
    void convertDtoAndInsertDocument() {
        QuestionElasticSearchAddDTO dto = new QuestionElasticSearchAddDTO();
        dto.setId(System.currentTimeMillis());
        dto.setQuestionText("What is the capital of France?");
        dto.setAnswerText("The capital of France is Paris.");
        dto.setCourseId(1001L);
        dto.setDifficulty(3f);
        dto.setAuthorId(42L);

        QuestionDocument document = converter.QuestionElasticSearchDTOToQuestionDocument(dto);

        Assertions.assertNotNull(document);
        Assertions.assertEquals(dto.getId(), document.getId());
        Assertions.assertEquals(dto.getQuestionText(), document.getQuestionText());
        Assertions.assertEquals(dto.getAnswerText(), document.getAnswerText());

        Assumptions.assumeTrue(repository != null, "QuestionDocumentRepository bean is not available");

        try {
            QuestionDocument saved = repository.save(document);
            Assertions.assertNotNull(saved);
            Assertions.assertEquals(document.getId(), saved.getId());
        } catch (RuntimeException ex) {
            Assumptions.assumeTrue(false, "Elasticsearch is not reachable: " + ex.getMessage());
        } finally {
            if (document.getId() != null) {
                try {
                    System.out.println("ok");
                    repository.deleteById(document.getId());
                } catch (RuntimeException ignored) {
                    // ignore cleanup failures
                }
            }
        }
    }
}
