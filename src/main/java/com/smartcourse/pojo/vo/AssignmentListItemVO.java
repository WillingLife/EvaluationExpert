package com.smartcourse.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentListItemVO {
    private Long assignmentId;
    private String name;
    private String status;
    private LocalDateTime deadline;
}