package com.smartcourse.pojo.dto.dify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyResponseDataDTO<T> {
    private String id;
    private String workflowId;
    private String status;
    private T outputs;
    private String error;
    private Double elapsedTime;
    private Integer totalTokens;
    private Integer totalSteps;
}
