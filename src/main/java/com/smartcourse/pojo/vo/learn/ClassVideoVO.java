package com.smartcourse.pojo.vo.learn;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClassVideoVO {
    Long studentId;
    String studentName;
    List<VideoProgressVO> videoProgressVOList;
}
