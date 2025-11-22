package com.smartcourse.service.impl;

import com.smartcourse.converter.QuestionElasticSearchConverter;
import com.smartcourse.exception.InternalErrorException;
import com.smartcourse.infra.es.QuestionDocumentEsClient;
import com.smartcourse.infra.es.QuestionKnowledgeEsClient;
import com.smartcourse.infra.es.vo.QuestionEsHit;
import com.smartcourse.infra.es.vo.QuestionEsSearchResult;
import com.smartcourse.infra.es.vo.QuestionKnowledgeHit;
import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.dto.reranker.BaseRerankerItem;
import com.smartcourse.pojo.query.QuestionElasticSearchQuery;
import com.smartcourse.pojo.query.QuestionKnowledgeSearchQuery;
import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.service.ElasticSearchQueryService;
import com.smartcourse.service.QuestionService;
import com.smartcourse.service.reranker.RerankerFactory;
import com.smartcourse.utils.HighlightUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElasticSearchQueryServiceImpl implements ElasticSearchQueryService {

    private static final Integer ES_FORM=0;
    private static final Integer ES_SIZE=200;
    private static final Integer ES_VECTOR_TOP_K=200;
    private static final Integer ES_VECTOR_CANDIDATES=500;
    private static final String QUESTION_TEXT="question_text";
    private static final String ANSWER_TEXT="answer_text";

    private final QuestionElasticSearchConverter  questionElasticSearchConverter;
    private final ObjectProvider<EmbeddingModel> embeddingModelProvider;
    private final QuestionDocumentEsClient questionDocumentEsClient;
    private final QuestionKnowledgeEsClient questionKnowledgeEsClient;
    private final QuestionService questionService;



    @Override
    public List<QuestionQueryESItemVO> queryQuestionDocument(QuestionElasticSearchQueryDTO dto) {
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
        if(Boolean.TRUE.equals(dto.getUseKnowledgeArgs())) {
            knowledgeSearch = questionKnowledgeEsClient.search(knowledgeSearchQuery);
        }
        Map<Long, Map<String, List<String>>> allHighlight = new HashMap<>();
        Map<Long, BaseRerankerItem> rawRecallResults = new HashMap<>();
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
        List<Long> idList = rerankedItems.stream()
                .map(BaseRerankerItem::getId)
                .toList();
        // 生成VO list
        Map<Long, BaseRerankerItem> rerankerMap = rerankedItems.stream()
                .collect(Collectors.toMap(BaseRerankerItem::getId, item -> item));
        List<QuestionQueryVO> batch = questionService.getBatch(idList);
        List<QuestionQueryESItemVO> res = new ArrayList<>();
        for (QuestionQueryVO questionQueryVO : batch) {
            QuestionQueryESItemVO vo = questionElasticSearchConverter.queryVoToESVo(questionQueryVO);
            BaseRerankerItem rerankerItem = rerankerMap.get(vo.getId());
            if (rerankerItem.getVectorRank() > 0 && rerankerItem.getKeywordRank() < 0) {
                vo.setVectorGet(true);
            }
            if (rerankerItem.getKnowledgeRank() > 0 && rerankerItem.getKeywordRank() < 0) {
                vo.setKnowledgeGet(true);
            }
            if (rerankerItem.getCfRank() > 0 && rerankerItem.getKeywordRank() < 0) {
                vo.setCfGet(true);
            }
            Map<String, List<String>> highlightItems = allHighlight.get(vo.getId());
            List<String> highlightStrings=null;
            if (highlightItems != null) {
                highlightStrings =  highlightItems.get(QUESTION_TEXT);
            }

            vo.setStem(HighlightUtil.applyHighlight(vo.getStem(),highlightStrings));
            res.add(vo);
        }
//        System.out.println(search);
        return res;

    }
}
