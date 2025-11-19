package com.smartcourse.pojo.query;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KnowledgeSearchQueryItem {
    private Long knowledgeId;
    private Double weight;
}
