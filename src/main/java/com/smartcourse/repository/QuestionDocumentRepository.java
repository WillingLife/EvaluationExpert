package com.smartcourse.repository;

import com.smartcourse.model.QuestionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface QuestionDocumentRepository extends ElasticsearchRepository<QuestionDocument, Long> {

}

