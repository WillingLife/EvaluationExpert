package com.smartcourse.pojo.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherGradeAssign {
    private Long teacherId;
    private Long examScoreId;
    private List<TeacherGradeItemDTO> grades;
}