package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonClassDescription("每个查询知识点对象")
public class ElasticSearchKnowledgeDTO {
    @JsonPropertyDescription("查询的知识点的Id，长整型")
    private Long knowledgeId;
    @JsonPropertyDescription("查询的知识点的权重，0-1的浮点数")
    private Double weight;
}
