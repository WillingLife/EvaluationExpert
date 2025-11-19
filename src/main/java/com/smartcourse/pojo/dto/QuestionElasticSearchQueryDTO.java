package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuestionElasticSearchQueryDTO {
    /** 课程 ID，null 表示不限。 */
    private Long courseId;

    /** 用户输入的检索关键词。 */
    private String query;

    /** 是否启用同义词搜索。 */
    private Boolean synonymy;

    /** 是否启用模糊搜索。 */
    private Boolean fuzzy;

    /** 题目类型，null 表示不限。 */
    private String type;

    /** 最低难度，null 表示不限。 */
    private Double lowDifficulty;

    /** 最高难度，null 表示不限。 */
    private Double highDifficulty;

    /** 是否检索答案字段。 */
    private Boolean searchAnswer;

    /** 是否启用向量检索。 */
    private Boolean useVector;

    /** 作者 ID，null 表示不限。 */
    private Long authorId;

    /** 是否使用知识节点查询 */
    private Boolean useKnowledgeArgs;

    /** 查询的知识节点*/
    private List<ElasticSearchKnowledgeDTO> knowledge;

    /** reranker 配置 */
    private QueryRerankerConfigDTO reranker;



}
