package com.smartcourse.pojo.vo.dify;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyMappingKnowledgeItemVO {
    private Long id;
    private Double weight;
}
