package com.smartcourse.service.reranker;

import com.smartcourse.enums.RerankStrategyEnum;
import com.smartcourse.pojo.dto.QueryRerankerConfigDTO;
import com.smartcourse.pojo.dto.reranker.BaseRerankerItem;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * 根据配置策略实例化具体 Reranker 的工厂。
 */
public final class RerankerFactory {

    private RerankerFactory() {
    }

    /**
     * 根据配置构建重排策略实例。
     *
     * @param config 调用方传入的重排配置
     * @return 对应策略的实现
     */
    public static Reranker build(QueryRerankerConfigDTO config) {
        String strategy = config == null ? null : config.getRerankStrategy();
        if (!StringUtils.hasText(strategy)) {
            return defaultReranker(config);
        }
        String normalized = strategy.trim().toLowerCase(Locale.ROOT);
        if (isWeightedFusion(normalized)) {
            return new WeightedFusionReranker(config);
        }
        if (isWrrf(normalized)) {
            return new WeightedReciprocalRankFusionReranker(config);
        }
        return defaultReranker(config);
    }

    /**
     * 便捷方法：直接构建策略并执行重排。
     */
    public static List<BaseRerankerItem> rerank(QueryRerankerConfigDTO config,
                                                List<BaseRerankerItem> candidates) {
        return build(config).rerank(candidates);
    }

    private static boolean isWeightedFusion(String normalizedStrategy) {
        String canonical = RerankStrategyEnum.WEIGHTED_FUSION.getValue();
        return canonical.equals(normalizedStrategy);
    }

    private static boolean isWrrf(String normalizedStrategy) {
        String canonical = RerankStrategyEnum.WRRF.getValue();
        return canonical.equals(normalizedStrategy);
    }

    private static Reranker defaultReranker(QueryRerankerConfigDTO config) {
        return new WeightedFusionReranker(config);
    }
}
