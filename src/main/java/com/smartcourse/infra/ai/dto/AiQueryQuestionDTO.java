package com.smartcourse.infra.ai.dto;


import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.pojo.dto.ElasticSearchKnowledgeDTO;
import com.smartcourse.pojo.dto.QueryRerankerConfigDTO;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonClassDescription("用来向数据库查询的参数,选择合适的参数")
public class AiQueryQuestionDTO {
    /** 课程 ID，null 表示不限。 */
    @JsonPropertyDescription("查询的课程id，长整型")
    private Long courseId;

    /** 用户输入的检索关键词。 */
    @JsonPropertyDescription("查询的关键词，多个关键词之间可以使用空格分隔，可以返回与查询关键词相关的题目")
    private String query;

    /** 题目类型，null 表示不限。 */
    @JsonPropertyDescription("查询的题目类型，null为不限制题目类型,题目类型有4种，分别是single，multiple，fill_blank，short_answer")
    private String type;

    /** 最低难度，null 表示不限。 */
    @JsonPropertyDescription("查询题目的最低难度(0-5)，null表示不限制")
    private Double lowDifficulty;

    /** 最高难度，null 表示不限。 */
    @JsonPropertyDescription("查询题目的最高难度(0-5)，null表示不限制")
    private Double highDifficulty;

    /** 是否使用知识节点查询 */
    @JsonPropertyDescription("是否启用知识点查询,布尔值")
    private Boolean useKnowledgeArgs=false;

    /** 查询的知识节点*/
    @JsonPropertyDescription("查询的知识点列表")
    private List<ElasticSearchKnowledgeDTO> knowledge;
}
