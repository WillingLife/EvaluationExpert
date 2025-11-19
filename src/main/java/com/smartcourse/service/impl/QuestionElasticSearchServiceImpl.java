package com.smartcourse.service.impl;
import com.smartcourse.converter.QuestionElasticSearchConverter;
import com.smartcourse.exception.InternalErrorException;
import com.smartcourse.infra.es.QuestionDocumentEsClient;
import com.smartcourse.infra.es.QuestionKnowledgeEsClient;
import com.smartcourse.infra.es.vo.QuestionEsHit;
import com.smartcourse.infra.es.vo.QuestionEsSearchResult;
import com.smartcourse.infra.es.vo.QuestionKnowledgeHit;
import com.smartcourse.pojo.dto.QuestionElasticSearchAddDTO;
import com.smartcourse.model.QuestionDocument;
import com.smartcourse.pojo.dto.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.dto.reranker.BaseRerankerItem;
import com.smartcourse.pojo.query.QuestionElasticSearchQuery;
import com.smartcourse.pojo.query.QuestionKnowledgeSearchQuery;
import com.smartcourse.repository.QuestionDocumentRepository;
import com.smartcourse.service.QuestionElasticSearchService;
import com.smartcourse.service.reranker.RerankerFactory;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionElasticSearchServiceImpl implements QuestionElasticSearchService {

    /**
     * 进行分割的最少字符数量
     */
    private static final int CHUNK_THRESHOLD = 1000;
    /**
     * 进行分割时每一块的字符数量
     */
    private static final int CHUNK_SIZE = 800;
    private static final int CHUNK_OVERLAP = 80;

    // 暂时使用常量
    private static final Integer ES_FORM=0;
    private static final Integer ES_SIZE=200;
    private static final Integer ES_VECTOR_TOP_K=200;
    private static final Integer ES_VECTOR_CANDIDATES=500;
    private static final String QUESTION_TEXT="question_text";
    private static final String ANSWER_TEXT="answer_text";

    private final QuestionDocumentRepository repository;
    private final ObjectProvider<EmbeddingModel> embeddingModelProvider;
    private final QuestionElasticSearchConverter questionElasticSearchConverter;
    private final DocumentSplitter documentSplitter = DocumentSplitters.recursive(CHUNK_SIZE, CHUNK_OVERLAP);
    private final QuestionDocumentEsClient questionDocumentEsClient;
    private final QuestionKnowledgeEsClient questionKnowledgeEsClient;

    @Override
    public void addQuestionDocument(QuestionElasticSearchAddDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("QuestionElasticSearchDTO 不能为空");
        }

        QuestionDocument doc = questionElasticSearchConverter.QuestionElasticSearchDTOToQuestionDocument(dto);

        EmbeddingModel model = embeddingModelProvider.getIfAvailable();
        boolean hasTextForEmbedding = StringUtils.hasText(dto.getQuestionText())
                || StringUtils.hasText(dto.getAnswerText());

        if (model == null && hasTextForEmbedding) {
            log.warn("嵌入模型bean不可用。跳过问题id的向量生成 {}", dto.getId());
        }

        doc.setQuestionVectorChunks(createQuestionChunks(dto.getQuestionText(), model));
        doc.setAnswerVectorChunks(createAnswerChunks(dto.getAnswerText(), model));

        repository.save(doc);
    }


    /**
     * 将文本转化为 {@link QuestionDocument.QuestionVectorChunk}列表
     *
     * @param text 题目文本
     * @param model embedding 模型
     * @return QuestionVector
     */
    private List<QuestionDocument.QuestionVectorChunk> createQuestionChunks(String text, EmbeddingModel model) {
        List<String> chunks = splitText(text);
        if (chunks.isEmpty()) {
            return null;
        }

        List<QuestionDocument.QuestionVectorChunk> vectorChunks = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i);
            QuestionDocument.QuestionVectorChunk chunk = new QuestionDocument.QuestionVectorChunk();
            chunk.setChunkId(i+1);
            chunk.setChunkText(chunkText);
            if (model != null) {
                chunk.setChunkVector(model.embed(chunkText));
            }
            vectorChunks.add(chunk);
        }
        return vectorChunks;
    }

    /**
     * 将答案文本切分为{@link QuestionDocument.AnswerVectorChunk}
     * @param text 答案文本
     * @param model embedding 模型
     * @return QuestionDocument.AnswerVectorChunk列表
     */
    private List<QuestionDocument.AnswerVectorChunk> createAnswerChunks(String text, EmbeddingModel model) {
        List<String> chunks = splitText(text);
        if (chunks.isEmpty()) {
            return null;
        }

        List<QuestionDocument.AnswerVectorChunk> vectorChunks = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i);
            QuestionDocument.AnswerVectorChunk chunk = new QuestionDocument.AnswerVectorChunk();
            chunk.setChunkId(i+1);
            chunk.setChunkText(chunkText);
            if (model != null) {
                chunk.setChunkVector(model.embed(chunkText));
            }
            vectorChunks.add(chunk);
        }
        return vectorChunks;
    }

    /**
     * 切分文本
     * @param text 待切分文本
     * @return
     */
    private List<String> splitText(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        String normalized = text.trim();
        if (normalized.length() <= CHUNK_THRESHOLD) {
            return List.of(normalized);
        }

        List<TextSegment> segments = documentSplitter.split(Document.from(normalized));
        return segments.stream()
                .map(TextSegment::text)
                .toList();
    }

    @Override
    public QuestionEsSearchResult queryQuestionDocument(QuestionElasticSearchQueryDTO dto) {
        QuestionElasticSearchQuery query = questionElasticSearchConverter.questionESQueryDTOToQuestionESQuery(dto);
        QuestionKnowledgeSearchQuery knowledgeSearchQuery= questionElasticSearchConverter.questionESQueryDTOToKnowledgeQuery(dto);
        query.setFrom(ES_FORM);
        query.setSize(ES_SIZE);
        query.setVectorTopK(ES_VECTOR_TOP_K);
        query.setVectorCandidates(ES_VECTOR_CANDIDATES);
        knowledgeSearchQuery.setSize(ES_SIZE);
        EmbeddingModel model = embeddingModelProvider.getIfAvailable();
        if(query.getUseVector() && StringUtils.hasText(query.getQuery())) {
            if(model != null) {
                query.setQueryVector(model.embed(query.getQuery()));
            }
            else {
                throw new InternalErrorException("Embedding模型不可用");
            }
        }
        // 文本和向量查询
        QuestionEsSearchResult search = questionDocumentEsClient.search(query);
        // 知识节点查询
        List<QuestionKnowledgeHit> knowledgeSearch = new ArrayList<>();
        if(Boolean.TRUE.equals(query.getUseVector())) {
            knowledgeSearch = questionKnowledgeEsClient.search(knowledgeSearchQuery);
        }
        Map<Long, Map<String, List<String>>> allHighlight = new HashMap<>();
        Map<Long,BaseRerankerItem> rawRecallResults = new HashMap<>();
        int count = 1;
        // 处理文本查询
        for(QuestionEsHit questionEsHit:search.textHits()){
            allHighlight.put(questionEsHit.document().getId(), questionEsHit.highlights());
            BaseRerankerItem item = rawRecallResults.computeIfAbsent(questionEsHit.document().getId(), k -> {
                BaseRerankerItem newItem = new BaseRerankerItem();
                newItem.setId(k);
                return newItem;
            });
            item.setKeywordScore(questionEsHit.score());
            item.setKeywordRank(count);
            count++;
        }
        // 处理向量查询
        if(dto.getUseVector()){
            count=1;
            for(QuestionEsHit questionEsHit:search.vectorHits()){
                BaseRerankerItem item = rawRecallResults.computeIfAbsent(questionEsHit.document().getId(), k->{
                    BaseRerankerItem newItem = new BaseRerankerItem();
                    newItem.setId(k);
                    return newItem;
                });
                item.setUseVector(true);
                item.setVectorScore(questionEsHit.score());
                item.setVectorRank(count);
                count++;
            }
        }
        // 处理知识点查询
        if(dto.getUseKnowledgeArgs()){
            count=1;
            for(QuestionKnowledgeHit knowledgeHit:knowledgeSearch){
                BaseRerankerItem item = rawRecallResults.computeIfAbsent(knowledgeHit.id(), k->{
                    BaseRerankerItem newItem = new BaseRerankerItem();
                    newItem.setId(k);
                    return newItem;
                });
                item.setUseKnowledge(true);
                item.setKnowledgeScore(knowledgeHit.score());
                item.setKnowledgeRank(count);
                count++;
            }
        }
        List<BaseRerankerItem> rerankerItems = new ArrayList<>(rawRecallResults.values());
        // 由reranker进行重排序
        List<BaseRerankerItem> rerankedItems = RerankerFactory.rerank(dto.getReranker(), rerankerItems);



//        System.out.println(search);
        return search;

    }

}


