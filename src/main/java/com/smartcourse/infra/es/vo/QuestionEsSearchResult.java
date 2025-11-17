package com.smartcourse.infra.es.vo;

import java.util.List;

/**
 * Elasticsearch 查询结果，区分文本与向量召回。
 */
public record QuestionEsSearchResult(List<QuestionEsHit> textHits,
                                     List<QuestionEsHit> vectorHits) {
}
