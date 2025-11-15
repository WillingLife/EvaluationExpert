package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    private Long id;
    private String name;
    private String description;
    private Long courseId;
    private Integer submitLimit;
    private String status;
    private Integer version;
    private Long creator;
    private LocalDateTime deadline;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean delete;
}