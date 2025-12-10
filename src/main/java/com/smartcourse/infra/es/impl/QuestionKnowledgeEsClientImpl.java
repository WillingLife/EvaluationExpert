package com.smartcourse.infra.es.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.json.JsonData;
import com.smartcourse.infra.es.QuestionKnowledgeEsClient;
import com.smartcourse.infra.es.vo.QuestionKnowledgeHit;
import com.smartcourse.model.QuestionKnowledgeDocument;
import com.smartcourse.pojo.query.KnowledgeSearchQueryItem;
import com.smartcourse.pojo.query.QuestionKnowledgeSearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionKnowledgeEsClientImpl implements QuestionKnowledgeEsClient {

    private static final String INDEX_NAME = "question_knowledge";
    private static final String FIELD_COURSE_ID = "course_id";
    private static final int DEFAULT_SIZE = 10;

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<QuestionKnowledgeHit> search(QuestionKnowledgeSearchQuery rawQuery) {
        QuestionKnowledgeSearchQuery query = rawQuery == null ? new QuestionKnowledgeSearchQuery() : rawQuery;
        if (CollectionUtils.isEmpty(query.getQueryItems())) {
            return List.of();
        }
        try {
            SearchRequest request = buildRequest(query);
            SearchResponse<QuestionKnowledgeDocument> response = elasticsearchClient.search(request, QuestionKnowledgeDocument.class);
            return toHits(response);
        } catch (ElasticsearchException e) {
            throw new IllegalStateException("Elasticsearch rejected knowledge search request", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute knowledge search", e);
        }
    }

    private SearchRequest buildRequest(QuestionKnowledgeSearchQuery query) {
        Map<String, Double> queryWeights = buildQueryWeights(query.getQueryItems());
        if (queryWeights.isEmpty()) {
            throw new IllegalArgumentException("queryItems must not be empty");
        }

        Query filter = buildFilterQuery(query.getCourseId());
        Query baseQuery = filter != null ? filter : Query.of(q -> q.matchAll(m -> m));

        return new SearchRequest.Builder()
                .index(INDEX_NAME)
                .size(resolveSize(query.getSize()))
                .query(q -> q.scriptScore(s -> s
                        .query(baseQuery)
                        .script(sc -> sc
                                .lang("painless")
                                .source(buildScoreScript())
                                .params("queryWeights", JsonData.of(queryWeights))
                        )
                ))
                .source(SourceConfig.of(s -> s.filter(f -> f.includes("id"))))
                .build();
    }

    private Map<String, Double> buildQueryWeights(List<KnowledgeSearchQueryItem> items) {
        Map<String, Double> weights = new LinkedHashMap<>();
        if (items == null) {
            return weights;
        }
        for (KnowledgeSearchQueryItem item : items) {
            if (item == null || item.getKnowledgeId() == null || item.getWeight() == null) {
                continue;
            }
            weights.put(String.valueOf(item.getKnowledgeId()), item.getWeight());
        }
        return weights;
    }

    private Query buildFilterQuery(Long courseId) {
        if (courseId == null) {
            return null;
        }
        BoolQuery.Builder bool = new BoolQuery.Builder();
        // 修复: Long 类型应该直接传递,不需要转 String
        bool.filter(f -> f.term(t -> t.field(FIELD_COURSE_ID).value(courseId)));
        return Query.of(q -> q.bool(bool.build()));
    }

    /**
     * 修复: 使用 _source 访问 nested 字段,保证 id 和 weight 的对应关系
     */
    private String buildScoreScript() {
        return "double score = 0.0; " +
                "if (params._source != null && params._source.knowledge_points != null) { " +
                "  for (def kp : params._source.knowledge_points) { " +
                "    if (kp.id != null && kp.weight != null) { " +
                "      def q = params.queryWeights.get(kp.id.toString()); " +
                "      if (q != null) { " +
                "        score += q * kp.weight; " +
                "      } " +
                "    } " +
                "  } " +
                "} " +
                "return score;";
    }

    private List<QuestionKnowledgeHit> toHits(SearchResponse<QuestionKnowledgeDocument> response) {
        if (response == null || response.hits() == null || CollectionUtils.isEmpty(response.hits().hits())) {
            return List.of();
        }
        List<QuestionKnowledgeHit> hits = new ArrayList<>();
        for (Hit<QuestionKnowledgeDocument> hit : response.hits().hits()) {
            Long id = extractId(hit);
            double score = hit.score() == null ? 0.0d : hit.score();
            if (id != null) {
                hits.add(new QuestionKnowledgeHit(id, score));
            }
        }
        return hits;
    }

    private Long extractId(Hit<QuestionKnowledgeDocument> hit) {
        if (hit == null) {
            return null;
        }
        if (hit.source() != null && hit.source().getId() != null) {
            return hit.source().getId();
        }
        try {
            return hit.id() == null ? null : Long.valueOf(hit.id());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse hit id: {}", hit.id());
            return null;
        }
    }

    private int resolveSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return size;
    }
}