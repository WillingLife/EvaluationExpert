package com.smartcourse.service;

import com.smartcourse.pojo.dto.QuestionElasticSearchAddDTO;
import com.smartcourse.pojo.dto.QuestionElasticSearchQueryDTO;

public interface QuestionElasticSearchService {

    /**
     * Indexes a question into Elasticsearch along with its embedding vectors.
     *
     * @param dto payload containing question, answer, and metadata
     */
    void addQuestionDocument(QuestionElasticSearchAddDTO dto);

    void queryQuestionDocument(QuestionElasticSearchQueryDTO dto);
}
