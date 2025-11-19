package com.smartcourse.utils;

import com.smartcourse.pojo.dto.reranker.BaseRerankerItem;

import java.util.List;

/**
 * reranker 分数归一化工具，当前提供 min-max 归一化实现。
 */
public final class RerankerScoreNormalizer {

    private RerankerScoreNormalizer() {
    }

    /**
     * 对传入的结果列表进行 min-max 归一化：score' = (score - min) / (max - min)。
     * 当所有有效分数相等时，默认归一化为 1.0。
     *
     * @param items 需要归一化的集合（支持 BaseRerankerItem 子类）
     * @return 归一化处理后的集合引用，方便链式调用
     */
    public static <T extends BaseRerankerItem> List<T> minMaxNormalize(List<T> items) {
        if (items == null || items.isEmpty()) {
            return items;
        }

        double minScore = Double.POSITIVE_INFINITY;
        double maxScore = Double.NEGATIVE_INFINITY;
        boolean hasValidScore = false;

        for (BaseRerankerItem item : items) {
            if (item == null || item.getScore() == null) {
                continue;
            }
            hasValidScore = true;
            double score = item.getScore();
            minScore = Math.min(minScore, score);
            maxScore = Math.max(maxScore, score);
        }

        if (!hasValidScore) {
            return items;
        }

        double range = maxScore - minScore;
        if (range == 0d) {
            for (BaseRerankerItem item : items) {
                if (item == null || item.getScore() == null) {
                    continue;
                }
                item.setScore(1d);
            }
            return items;
        }

        for (BaseRerankerItem item : items) {
            if (item == null || item.getScore() == null) {
                continue;
            }
            double normalized = (item.getScore() - minScore) / range;
            item.setScore(normalized);
        }
        return items;
    }
}
