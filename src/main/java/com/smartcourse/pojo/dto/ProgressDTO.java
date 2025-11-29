package com.smartcourse.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProgressDTO {
    private Double elapsed;
    private Double duration;
    private List<List<Double>> segments;
    private Double completion;
}
