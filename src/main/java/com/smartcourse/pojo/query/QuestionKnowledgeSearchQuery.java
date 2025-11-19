package com.smartcourse.pojo.query;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuestionKnowledgeSearchQuery {
    private Long courseId;
    private Integer size;
    private List<KnowledgeSearchQueryItem> queryItems;
}
