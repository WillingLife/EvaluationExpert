package com.smartcourse.service.reranker;

import com.smartcourse.pojo.dto.QueryRerankerConfigDTO;
import com.smartcourse.pojo.dto.reranker.BaseRerankerItem;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * WRRF（Weighted Reciprocal Rank Fusion）策略。
 */
public class WeightedReciprocalRankFusionReranker implements Reranker {

    private static final double DEFAULT_RRF_CONSTANT = 60d;

    private final Map<String, Double> weightMap;
    private final double rrfConstant;

    public WeightedReciprocalRankFusionReranker(QueryRerankerConfigDTO config) {
        this(config, DEFAULT_RRF_CONSTANT);
    }

    public WeightedReciprocalRankFusionReranker(QueryRerankerConfigDTO config, double rrfConstant) {
        this.weightMap = RerankerWeights.resolveWeights(config);
        this.rrfConstant = rrfConstant <= 0 ? DEFAULT_RRF_CONSTANT : rrfConstant;
    }

    @Override
    public List<BaseRerankerItem> rerank(List<BaseRerankerItem> candidates) {
        if (CollectionUtils.isEmpty(candidates)) {
            return Collections.emptyList();
        }

        List<BaseRerankerItem> ranked = new ArrayList<>();
        for (BaseRerankerItem candidate : candidates) {
            BaseRerankerItem scored = applyReciprocalRankScore(candidate);
            if (scored != null && scored.getScore() != null) {
                ranked.add(scored);
            }
        }

        ranked.sort((first, second) -> Double.compare(
                Objects.requireNonNullElse(second.getScore(), 0d),
                Objects.requireNonNullElse(first.getScore(), 0d)));
        return ranked;
    }

    private BaseRerankerItem applyReciprocalRankScore(BaseRerankerItem candidate) {
        if (candidate == null || candidate.getId() == null) {
            return null;
        }

        double fusedScore = 0d;
        boolean contributed = false;

        double keywordWeight = weight(RerankerWeights.SOURCE_KEYWORD);
        if (shouldUseRank(candidate.getKeywordRank(), keywordWeight, Boolean.TRUE)) {
            fusedScore += reciprocalContribution(keywordWeight, candidate.getKeywordRank());
            contributed = true;
        }

        double vectorWeight = weight(RerankerWeights.SOURCE_VECTOR);
        if (shouldUseRank(candidate.getVector_rank(), vectorWeight, candidate.getUseVector())) {
            fusedScore += reciprocalContribution(vectorWeight, candidate.getVector_rank());
            contributed = true;
        }

        double knowledgeWeight = weight(RerankerWeights.SOURCE_KNOWLEDGE);
        if (shouldUseRank(candidate.getKnowledgeRank(), knowledgeWeight, candidate.getUseKnowledge())) {
            fusedScore += reciprocalContribution(knowledgeWeight, candidate.getKnowledgeRank());
            contributed = true;
        }

        double cfWeight = weight(RerankerWeights.SOURCE_CF);
        if (shouldUseRank(candidate.getCfRank(), cfWeight, candidate.getUseCf())) {
            fusedScore += reciprocalContribution(cfWeight, candidate.getCfRank());
            contributed = true;
        }

        if (!contributed) {
            candidate.setScore(null);
            return null;
        }

        candidate.setScore(fusedScore);
        return candidate;
    }

    private double reciprocalContribution(double weight, Integer rank) {
        return weight / (rrfConstant + rank);
    }

    private double weight(String sourceKey) {
        return weightMap.getOrDefault(sourceKey, RerankerWeights.DEFAULT_WEIGHT);
    }

    private boolean shouldUseRank(Integer rank, double channelWeight, Boolean enabled) {
        if (rank == null || rank < 0 || channelWeight == 0d) {
            return false;
        }
        return enabled == null || Boolean.TRUE.equals(enabled);
    }
}
