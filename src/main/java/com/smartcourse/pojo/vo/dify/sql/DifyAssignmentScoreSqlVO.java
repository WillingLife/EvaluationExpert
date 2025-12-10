package com.smartcourse.pojo.vo.dify.sql;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyAssignmentScoreSqlVO {
    private Long assignmentScoreId;
    private String objectName;
    private String description;
}
