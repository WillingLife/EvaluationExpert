package com.smartcourse.repository;

import com.smartcourse.model.QuestionKnowledgeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface QuestionKnowledgeRepository extends ElasticsearchRepository<QuestionKnowledgeDocument, Long> {
}
