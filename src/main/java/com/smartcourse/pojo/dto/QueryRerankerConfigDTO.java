package com.smartcourse.pojo.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QueryRerankerConfigDTO {
    private String rerankStrategy="wighted_fusion";
    private Double keywordWeight=1.0;
    private Double vectorWeight=1.0;
    private Double knowledgeWeight=1.0;
    private Double cfScore=1.0;
}
