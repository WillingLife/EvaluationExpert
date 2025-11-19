package com.smartcourse.service.reranker;

import com.smartcourse.pojo.dto.reranker.BaseRerankerItem;

import java.util.List;

/**
 * reranker 策略接口，负责对多路召回结果进行融合排序。
 */
public interface Reranker {

    /**
     * 根据具体策略对输入结果重新排序。
     *
     * @param candidates 候选集合，每条记录包含多路召回信息
     * @return 融合排序后的结果
     */
    List<BaseRerankerItem> rerank(List<BaseRerankerItem> candidates);
}
