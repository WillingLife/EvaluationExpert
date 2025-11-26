package com.smartcourse.pojo.vo.dify;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GradeAssignmentDetailsVO {
    private GradeAssignmentDetailItemVO content;
    private GradeAssignmentDetailItemVO cover;
    private GradeAssignmentDetailItemVO depth;
    private GradeAssignmentDetailItemVO logic;
    private GradeAssignmentDetailItemVO creative;
}
