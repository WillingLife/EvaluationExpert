package com.smartcourse.pojo.vo.teacher.assignment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AssignmentDetectDetailVO {
    private Long leftAssignmentId;
    private Long rightAssignmentId;
    private String diff;
    private String leftContent;
    private String rightContent;
}
