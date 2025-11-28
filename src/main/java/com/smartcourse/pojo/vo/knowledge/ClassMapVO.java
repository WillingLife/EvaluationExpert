package com.smartcourse.pojo.vo.knowledge;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClassMapVO {
    private List<ClassNodeVO> nodes;
    private List<EdgeVO> edges;
    private Long studentNumber;
}
