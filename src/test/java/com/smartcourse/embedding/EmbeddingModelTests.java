package com.smartcourse.embedding;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Assertions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmbeddingModelTests {

    @Autowired
    private ObjectProvider<EmbeddingModel> embeddingModelProvider;

    @Test
    void embedHelloShouldReturnVector() {
        EmbeddingModel model = embeddingModelProvider.getIfAvailable();
        Assumptions.assumeTrue(model != null, "EmbeddingModel bean is not available in the test context");

        float[] vector = model.embed("hello");
        System.out.println(vector.length);

        Assertions.assertNotNull(vector, "Embedding result should not be null");
        Assertions.assertTrue(vector.length > 0, "Embedding result should contain at least one dimension");
    }
}
