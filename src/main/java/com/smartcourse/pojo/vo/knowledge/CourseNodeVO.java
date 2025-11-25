package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

import java.util.List;

@Data
public class CourseNodeVO {
    private Long id;
    private Double allScore;
    private Double getScore;
    private List<Clazz> getScoreClass;
    private List<Double> getScoreStudent;
}
