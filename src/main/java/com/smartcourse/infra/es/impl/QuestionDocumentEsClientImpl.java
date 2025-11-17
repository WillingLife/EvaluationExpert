package com.smartcourse.infra.es.impl;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.MsearchRequest;
import co.elastic.clients.elasticsearch.core.MsearchResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.msearch.MultiSearchItem;
import co.elastic.clients.elasticsearch.core.msearch.MultiSearchResponseItem;
import co.elastic.clients.elasticsearch.core.msearch.MultisearchBody;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.smartcourse.infra.es.QuestionDocumentEsClient;
import com.smartcourse.infra.es.vo.QuestionEsHit;
import com.smartcourse.infra.es.vo.QuestionEsSearchResult;
import com.smartcourse.model.QuestionDocument;
import com.smartcourse.pojo.query.QuestionElasticSearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionDocumentEsClientImpl implements QuestionDocumentEsClient {

    private static final String INDEX_NAME = "question_bank";
    private static final String FIELD_QUESTION_TEXT = "question_text";
    private static final String FIELD_QUESTION_TEXT_SYN = "question_text.syn";
    private static final String FIELD_ANSWER_TEXT = "answer_text";
    private static final String FIELD_ANSWER_TEXT_SYN = "answer_text.syn";
    private static final String FIELD_COURSE_ID = "course_id";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_AUTHOR_ID = "author_id";
    private static final String FIELD_DIFFICULTY = "difficulty";
    private static final String FIELD_QUESTION_VECTOR = "question_vector_chunks.chunk_vector";
    private static final String FIELD_ANSWER_VECTOR = "answer_vector_chunks.chunk_vector";
    private static final Set<String> HIGHLIGHT_FIELDS = Set.of(FIELD_QUESTION_TEXT, FIELD_ANSWER_TEXT);
    private static final int DEFAULT_FROM = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_VECTOR_TOP_K = 20;
    private static final int DEFAULT_VECTOR_NUM_CANDIDATES = 200;

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public QuestionEsSearchResult search(QuestionElasticSearchQuery rawQuery) {
        QuestionElasticSearchQuery query = rawQuery == null ? new QuestionElasticSearchQuery() : rawQuery;
        try {
            if (shouldUseVector(query)) {
                // hybrid recall 分支
                MsearchRequest request = buildHybridRequest(query);
                MsearchResponse<QuestionDocument> response = elasticsearchClient.msearch(request, QuestionDocument.class);
                List<MultiSearchResponseItem<QuestionDocument>> responses = response.responses();
                List<QuestionEsHit> textHits = responses.isEmpty() ? List.of() : toHits(responses.get(0));
                List<QuestionEsHit> vectorHits = responses.size() < 2 ? List.of() : toHits(responses.get(1));
                return new QuestionEsSearchResult(textHits, vectorHits);
            }
            SearchRequest request = buildTextSearchRequest(query);
            SearchResponse<QuestionDocument> response = elasticsearchClient.search(request, QuestionDocument.class);
            return new QuestionEsSearchResult(toHits(response), List.of());
        } catch (ElasticsearchException e) {
            throw new IllegalStateException("Elasticsearch rejected search request", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute question search", e);
        }
    }

    private SearchRequest buildTextSearchRequest(QuestionElasticSearchQuery query) {
        Query textQuery = buildKeywordQuery(query);
        return new SearchRequest.Builder()
                .index(INDEX_NAME)
                .from(resolveFrom(query.getFrom()))
                .size(resolveSize(query.getSize()))
                .query(textQuery)
                .highlight(buildHighlight())
                .build();
    }

    /**
     * 根据传入Query构建一个Hybrid搜索MsearchRequest
     */
    private MsearchRequest buildHybridRequest(QuestionElasticSearchQuery query) {
        MultisearchBody textBody = buildTextBody(query);
        MultisearchBody vectorBody = buildVectorBody(query);
        return new MsearchRequest.Builder()
                .searches(s -> s.header(h -> h.index(INDEX_NAME)).body(textBody))
                .searches(s -> s.header(h -> h.index(INDEX_NAME)).body(vectorBody))
                .build();
    }

    /**
     * 构建文本搜索的 hybrid 搜索体
     */
    private MultisearchBody buildTextBody(QuestionElasticSearchQuery query) {
        return new MultisearchBody.Builder()
                .from(resolveFrom(query.getFrom()))
                .size(resolveSize(query.getSize()))
                .query(buildKeywordQuery(query))
                .highlight(buildHighlight())
                .build();
    }

    /**
     * 构建向量搜索的hybrid搜索体
     */
    private MultisearchBody buildVectorBody(QuestionElasticSearchQuery query) {
        float[] vector = query.getQueryVector();
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("向量检索需要 queryVector");
        }
        List<Float> queryVector = toFloatList(vector);
        Query filter = buildFilterQuery(query);
        int topK = resolveVectorTopK(query.getVectorTopK());
        int numCandidates = resolveVectorCandidates(query.getVectorCandidates(), topK);

        MultisearchBody.Builder builder = new MultisearchBody.Builder()
                .size(topK);
        if (filter != null) {
            builder.query(filter);
        }
        builder.knn(buildKnn(fieldQuestionVector(), queryVector, topK, numCandidates));
        if (Boolean.TRUE.equals(query.getSearchAnswer())) {
            builder.knn(buildKnn(fieldAnswerVector(), queryVector, topK, numCandidates));
        }
        return builder.build();
    }

    private KnnSearch buildKnn(String field,
                               List<Float> vector,
                               int topK,
                               int numCandidates) {
        return new KnnSearch.Builder()
                .field(field)
                .queryVector(vector)
                .k(topK)
                .numCandidates(numCandidates)
                .build();
    }

    private Query buildKeywordQuery(QuestionElasticSearchQuery query) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
        boolean hasFilter = applyFilterClauses(bool, query);
        boolean hasShould = applyShouldClauses(bool, query);
        if (!hasFilter && !hasShould) {
            return Query.of(q -> q.matchAll(m -> m));
        }
        if (hasShould) {
            bool.minimumShouldMatch("1");
        }
        return Query.of(q -> q.bool(bool.build()));
    }

    private Query buildFilterQuery(QuestionElasticSearchQuery query) {
        BoolQuery.Builder bool = new BoolQuery.Builder();
        boolean hasFilter = applyFilterClauses(bool, query);
        return hasFilter ? Query.of(q -> q.bool(bool.build())) : null;
    }

    private boolean applyFilterClauses(BoolQuery.Builder bool, QuestionElasticSearchQuery query) {
        boolean hasFilter = false;
        if (query.getCourseId() != null) {
            bool.filter(f -> f.term(t -> t.field(fieldCourseId()).value(String.valueOf(query.getCourseId()))));
            hasFilter = true;
        }
        if (query.getAuthorId() != null) {
            bool.filter(f -> f.term(t -> t.field(fieldAuthorId()).value(String.valueOf(query.getAuthorId()))));
            hasFilter = true;
        }
        if (StringUtils.hasText(query.getType())) {
            bool.filter(f -> f.term(t -> t.field(fieldType()).value(query.getType())));
            hasFilter = true;
        }
        if (query.getLowDifficulty() != null || query.getHighDifficulty() != null) {
            // 如果 difficulty 是数字类型，使用 number()
            bool.filter(f -> f.range(r -> r.number(n -> {
                n.field(fieldDifficulty());
                if (query.getLowDifficulty() != null) {
                    n.gte(query.getLowDifficulty());
                }
                if (query.getHighDifficulty() != null) {
                    n.lte(query.getHighDifficulty());
                }
                return n;
            })));
            hasFilter = true;
        }
        return hasFilter;
    }

    private boolean applyShouldClauses(BoolQuery.Builder bool, QuestionElasticSearchQuery query) {
        if (!StringUtils.hasText(query.getQuery())) {
            return false;
        }
        String keyword = query.getQuery().trim();
        bool.should(s -> s.match(m -> m.field(fieldQuestionText()).query(keyword).boost(2.0f)));
        if (Boolean.TRUE.equals(query.getSynonymy())) {
            bool.should(s -> s.match(m -> m.field(fieldQuestionTextSyn()).query(keyword).boost(1.0f)));
        }
        if (Boolean.TRUE.equals(query.getFuzzy())) {
            bool.should(s -> s.match(m -> m.field(fieldQuestionText()).query(keyword).fuzziness("AUTO").prefixLength(1).boost(0.5f)));
        }
        if (Boolean.TRUE.equals(query.getSearchAnswer())) {
            bool.should(s -> s.match(m -> m.field(fieldAnswerText()).query(keyword)));
            if (Boolean.TRUE.equals(query.getSynonymy())) {
                bool.should(s -> s.match(m -> m.field(fieldAnswerTextSyn()).query(keyword)));
            }
        }
        return true;
    }

    private Highlight buildHighlight() {
        Highlight.Builder builder = new Highlight.Builder();
        builder.fields(fieldQuestionText(), new HighlightField.Builder().build());
        builder.fields(fieldAnswerText(), new HighlightField.Builder().build());
        return builder.build();
    }

    private List<QuestionEsHit> toHits(MultiSearchResponseItem<QuestionDocument> item) {
        if (item == null || !item.isResult()) {
            return List.of();         // response slot 是 failure 或 null，直接空列表
        }
        MultiSearchItem<QuestionDocument> multiItem = item.result();
        if (multiItem == null || multiItem.hits() == null) {
            return List.of();         // 解析失败或无 hits
        }
        SearchResponse<QuestionDocument> searchResponse = new SearchResponse.Builder<QuestionDocument>()
                .hits(multiItem.hits())
                .aggregations(multiItem.aggregations())
                .took(multiItem.took())
                .timedOut(multiItem.timedOut())
                .build();
        return toHits(searchResponse);
    }

    private List<QuestionEsHit> toHits(SearchResponse<QuestionDocument> response) {
        if (response == null || response.hits() == null || CollectionUtils.isEmpty(response.hits().hits())) {
            return List.of();
        }
        List<QuestionEsHit> results = new ArrayList<>();
        for (Hit<QuestionDocument> hit : response.hits().hits()) {
            QuestionDocument doc = hit.source();
            if (doc == null) {
                continue;
            }
            double score = hit.score() == null ? 0.0d : hit.score();
            Map<String, List<String>> highlight = filterHighlight(hit.highlight());
            results.add(new QuestionEsHit(doc, score, highlight));
        }
        return results;
    }

    private Map<String, List<String>> filterHighlight(Map<String, List<String>> highlight) {
        if (highlight == null || highlight.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> filtered = new LinkedHashMap<>();
        for (String field : HIGHLIGHT_FIELDS) {
            List<String> fragments = highlight.get(field);
            if (!CollectionUtils.isEmpty(fragments)) {
                filtered.put(field, List.copyOf(fragments));
            }
        }
        return filtered.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(filtered);
    }

    /**
     * 判断是否使用向量检索
     */
    private boolean shouldUseVector(QuestionElasticSearchQuery query) {
        return Boolean.TRUE.equals(query.getUseVector())
                && query.getQueryVector() != null
                && query.getQueryVector().length > 0;
    }

    private int resolveFrom(Integer from) {
        return (from == null || from < 0) ? DEFAULT_FROM : from;
    }

    private int resolveSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private int resolveVectorTopK(Integer topK) {
        if (topK == null || topK <= 0) {
            return DEFAULT_VECTOR_TOP_K;
        }
        return Math.min(topK, MAX_SIZE);
    }

    private int resolveVectorCandidates(Integer candidates, int topK) {
        int resolved = (candidates == null || candidates <= 0) ? DEFAULT_VECTOR_NUM_CANDIDATES : candidates;
        return Math.max(resolved, topK);
    }

    private List<Float> toFloatList(float[] vector) {
        List<Float> list = new ArrayList<>(vector.length);
        for (float v : vector) {
            list.add(v);
        }
        return list;
    }

    private String fieldQuestionText() {
        return FIELD_QUESTION_TEXT;
    }

    private String fieldQuestionTextSyn() {
        return FIELD_QUESTION_TEXT_SYN;
    }

    private String fieldAnswerText() {
        return FIELD_ANSWER_TEXT;
    }

    private String fieldAnswerTextSyn() {
        return FIELD_ANSWER_TEXT_SYN;
    }

    private String fieldCourseId() {
        return FIELD_COURSE_ID;
    }

    private String fieldType() {
        return FIELD_TYPE;
    }

    private String fieldAuthorId() {
        return FIELD_AUTHOR_ID;
    }

    private String fieldDifficulty() {
        return FIELD_DIFFICULTY;
    }

    private String fieldQuestionVector() {
        return FIELD_QUESTION_VECTOR;
    }

    private String fieldAnswerVector() {
        return FIELD_ANSWER_VECTOR;
    }
}

