package com.smartcourse.service.reranker;

import com.smartcourse.pojo.dto.QueryRerankerConfigDTO;
import com.smartcourse.pojo.dto.reranker.BaseRerankerItem;
import com.smartcourse.utils.RerankerScoreNormalizer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 将多路召回结果按照权重进行线性融合的重排实现。
 */
public class WeightedFusionReranker implements Reranker {

    private final Map<String, Double> weightMap;

    public WeightedFusionReranker(QueryRerankerConfigDTO config) {
        this.weightMap = RerankerWeights.resolveWeights(config);
    }

    @Override
    public List<BaseRerankerItem> rerank(List<BaseRerankerItem> candidates) {
        if (CollectionUtils.isEmpty(candidates)) {
            return Collections.emptyList();
        }

        ChannelScores normalizedScores = new ChannelScores(
                normalizeChannel(candidates, BaseRerankerItem::getKeywordScore),
                normalizeChannel(candidates, BaseRerankerItem::getVectorScore),
                normalizeChannel(candidates, BaseRerankerItem::getKnowledgeScore),
                normalizeChannel(candidates, BaseRerankerItem::getCfScore));

        List<BaseRerankerItem> ranked = new ArrayList<>();
        for (BaseRerankerItem candidate : candidates) {
            BaseRerankerItem scored = applyFusionScore(candidate, normalizedScores);
            if (scored != null && scored.getScore() != null) {
                ranked.add(scored);
            }
        }

        RerankerScoreNormalizer.minMaxNormalize(ranked);
        ranked.sort((first, second) -> Double.compare(
                Objects.requireNonNullElse(second.getScore(), 0d),
                Objects.requireNonNullElse(first.getScore(), 0d)));
        return ranked;
    }

    private BaseRerankerItem applyFusionScore(BaseRerankerItem candidate, ChannelScores normalized) {
        if (candidate == null || candidate.getId() == null) {
            return null;
        }

        double fusedScore = 0d;
        boolean contributed = false;

        double keywordWeight = weight(RerankerWeights.SOURCE_KEYWORD);
        Double keywordScore = normalized.keywordScores().get(candidate.getId());
        if (shouldUseChannel(keywordScore, keywordWeight, Boolean.TRUE)) {
            fusedScore += keywordScore * keywordWeight;
            contributed = true;
        }

        double vectorWeight = weight(RerankerWeights.SOURCE_VECTOR);
        Double vectorScore = normalized.vectorScores().get(candidate.getId());
        if (shouldUseChannel(vectorScore, vectorWeight, candidate.getUseVector())) {
            fusedScore += vectorScore * vectorWeight;
            contributed = true;
        }

        double knowledgeWeight = weight(RerankerWeights.SOURCE_KNOWLEDGE);
        Double knowledgeScore = normalized.knowledgeScores().get(candidate.getId());
        if (shouldUseChannel(knowledgeScore, knowledgeWeight, candidate.getUseKnowledge())) {
            fusedScore += knowledgeScore * knowledgeWeight;
            contributed = true;
        }

        double cfWeight = weight(RerankerWeights.SOURCE_CF);
        Double cfScore = normalized.cfScores().get(candidate.getId());
        if (shouldUseChannel(cfScore, cfWeight, candidate.getUseCf())) {
            fusedScore += cfScore * cfWeight;
            contributed = true;
        }

        if (!contributed) {
            candidate.setScore(null);
            return null;
        }

        candidate.setScore(fusedScore);
        return candidate;
    }

    private double weight(String sourceKey) {
        return weightMap.getOrDefault(sourceKey, RerankerWeights.DEFAULT_WEIGHT);
    }

    private boolean shouldUseChannel(Double channelScore, double channelWeight, Boolean enabled) {
        if (channelScore == null || channelWeight == 0d) {
            return false;
        }
        return enabled == null || Boolean.TRUE.equals(enabled);
    }

    private Map<Integer, Double> normalizeChannel(List<BaseRerankerItem> candidates,
                                                  Function<BaseRerankerItem, Double> extractor) {
        List<BaseRerankerItem> channelItems = new ArrayList<>();
        for (BaseRerankerItem candidate : candidates) {
            if (candidate == null || candidate.getId() == null) {
                continue;
            }
            Double score = extractor.apply(candidate);
            if (score == null) {
                continue;
            }
            BaseRerankerItem temp = new BaseRerankerItem();
            temp.setId(candidate.getId());
            temp.setScore(score);
            channelItems.add(temp);
        }
        if (channelItems.isEmpty()) {
            return Collections.emptyMap();
        }
        RerankerScoreNormalizer.minMaxNormalize(channelItems);
        return channelItems.stream()
                .collect(Collectors.toMap(BaseRerankerItem::getId,
                        BaseRerankerItem::getScore,
                        (existing, replacement) -> replacement));
    }

    private record ChannelScores(Map<Integer, Double> keywordScores,
                                 Map<Integer, Double> vectorScores,
                                 Map<Integer, Double> knowledgeScores,
                                 Map<Integer, Double> cfScores) {
    }
}
