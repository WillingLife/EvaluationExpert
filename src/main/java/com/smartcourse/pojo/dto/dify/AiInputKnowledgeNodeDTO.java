package com.smartcourse.pojo.dto.dify;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AiInputKnowledgeNodeDTO {
    private Long id;
    private String name;
}
