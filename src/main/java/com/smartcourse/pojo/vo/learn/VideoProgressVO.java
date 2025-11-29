package com.smartcourse.pojo.vo.learn;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.smartcourse.pojo.dto.ProgressDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoProgressVO {
    private Long id;
    private Long resourceId;
    private Long studentId;
    private Integer completion;
    private ProgressDTO progress;
    private LocalDateTime lastViewTime;
}
