package com.smartcourse.pojo.vo.teacher.assignment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.apache.poi.poifs.filesystem.DocumentOutputStream;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeacherAssignDetectItemVO {
    private Long leftStudentId;
    private String leftStudentName;
    private String leftClassName;
    private Long rightStudentId;
    private String rightStudentName;
    private String rightClassName;
    private Double score;
}
