package com.smartcourse.pojo.dto.reranker;

import lombok.Data;

@Data
public class BaseRerankerItem {
    private Long id;
    /**
     * 是否有向量检索参与
     */
    private Boolean useVector=false;
    /**
     * 是否有知识点检索参与
     */
    private Boolean useKnowledge=false;
    /**
     * 是否使用协同过滤算法
     */
    private Boolean useCf=false;
    //一定有关键词检索
    private Double keywordScore =0.0;
    /**
     * 0表示不在recall段
     */
    private Double vectorScore =0.0;
    private Double knowledgeScore =0.0;
    private Double cfScore =0.0;
    /**
     * -1表示不存在
     */
    private Integer keywordRank =-1;
    private Integer vectorRank=-1;
    private Integer knowledgeRank =-1;
    private Integer cfRank =-1;
    // 下面的参数仅仅在bge ranker使用
    private String query;
    private String source;
    /**
     * reranker 融合结果分数
     */
    private Double score;
}
