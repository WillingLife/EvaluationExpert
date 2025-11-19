package com.smartcourse.infra.es;

import com.smartcourse.infra.es.vo.QuestionEsSearchResult;
import com.smartcourse.pojo.query.QuestionElasticSearchQuery;

/**
 * 针对题库文档的 Elasticsearch 查询组件。
 */
public interface QuestionDocumentEsClient {

    /**
     * 组合关键词召回与可选向量召回，返回高亮结果。
     */
    QuestionEsSearchResult search(QuestionElasticSearchQuery query);
}
