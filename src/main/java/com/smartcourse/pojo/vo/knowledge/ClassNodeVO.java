package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

import java.util.List;

@Data
public class ClassNodeVO {
    private Long id;
    private Double allScore;
    private Double getScore;
    private List<Double> getScoreList;
}
