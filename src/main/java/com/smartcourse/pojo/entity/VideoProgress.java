package com.smartcourse.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoProgress {
    private Long id;
    private Long resourceId;
    private Long studentId;
    private Integer completion;
    private String progress;
    private LocalDateTime lastViewTime;
}
