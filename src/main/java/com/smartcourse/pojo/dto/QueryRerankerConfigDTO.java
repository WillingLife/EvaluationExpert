package com.smartcourse.pojo.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QueryRerankerConfigDTO {
    private String rerankStrategy;
    private Double keywordWeight;
    private Double vectorWeight;
    private Double knowledgeWeight;
    private Double cfScore;
}
