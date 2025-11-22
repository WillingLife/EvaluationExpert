package com.smartcourse.pojo.vo.dify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DifyMappingKnowledgeVO {
    private List<DifyMappingKnowledgeItemVO> nodes;
}
