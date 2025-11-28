package com.smartcourse.pojo.vo.knowledge;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CourseNodeVO {
    private Long id;
    private Double allScore;
    private Double getScore;
    private List<Clazz> getScoreClass;
    private List<Double> getScoreStudent;
}
