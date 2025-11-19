package com.smartcourse.pojo.dto.dify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyCompletionResponse<T> {
    private String workflowRunId;
    private String taskId;
    private DifyResponseDataDTO<T> data;

}
