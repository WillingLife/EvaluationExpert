package com.smartcourse.infra.es;

import com.smartcourse.infra.es.vo.QuestionKnowledgeHit;
import com.smartcourse.pojo.query.QuestionKnowledgeSearchQuery;

import java.util.List;

public interface QuestionKnowledgeEsClient {
    List<QuestionKnowledgeHit> search(QuestionKnowledgeSearchQuery query);
}
