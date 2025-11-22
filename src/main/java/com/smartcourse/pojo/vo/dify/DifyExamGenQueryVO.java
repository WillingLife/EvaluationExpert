package com.smartcourse.pojo.vo.dify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.pojo.query.KnowledgeSearchQueryItem;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyExamGenQueryVO {
    private String query;
    private String type;
    private Double lowDifficulty;
    private Double highDifficulty;
    private Boolean useKnowledgeArgs;
    private List<KnowledgeSearchQueryItem> knowledge;
    private String context;
}
