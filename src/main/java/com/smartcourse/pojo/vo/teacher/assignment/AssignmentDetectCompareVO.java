package com.smartcourse.pojo.vo.teacher.assignment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignmentDetectCompareVO {
    private String leftName;
    private String rightName;
    private String diff;
}
