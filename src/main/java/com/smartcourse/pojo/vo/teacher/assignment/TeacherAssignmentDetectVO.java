package com.smartcourse.pojo.vo.teacher.assignment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherAssignmentDetectVO {
    private Long assignmentId;
    private List<TeacherAssignDetectItemVO> list;
}
