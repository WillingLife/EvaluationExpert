package com.smartcourse.pojo.vo.knowledge;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClassMap {
    private String questionType;
    private Integer difficulty;
    private String stem;
    private BigDecimal totalScore;
    private BigDecimal score;
    private Double weight;
    private List<BigDecimal> studentScore;
}
