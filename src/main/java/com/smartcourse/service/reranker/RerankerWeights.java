package com.smartcourse.service.reranker;

import com.smartcourse.pojo.dto.QueryRerankerConfigDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一解析各检索来源的权重配置。
 */
final class RerankerWeights {

    static final String SOURCE_KEYWORD = "keyword";
    static final String SOURCE_VECTOR = "vector";
    static final String SOURCE_KNOWLEDGE = "knowledge";
    static final String SOURCE_CF = "cf";
    static final double DEFAULT_WEIGHT = 0.0d;

    private RerankerWeights() {
    }

    static Map<String, Double> resolveWeights(QueryRerankerConfigDTO config) {
        if (config == null) {
            return Collections.emptyMap();
        }

        Map<String, Double> weightMap = new HashMap<>();
        weightMap.put(SOURCE_KEYWORD, valueOrDefault(config.getKeywordWeight()));
        weightMap.put(SOURCE_VECTOR, valueOrDefault(config.getVectorWeight()));
        weightMap.put(SOURCE_KNOWLEDGE, valueOrDefault(config.getKnowledgeWeight()));
        weightMap.put(SOURCE_CF, valueOrDefault(config.getCfScore()));
        return weightMap;
    }

    private static double valueOrDefault(Double value) {
        return value == null ? DEFAULT_WEIGHT : value;
    }
}
