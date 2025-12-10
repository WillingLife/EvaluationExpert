package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

@Data
public class EdgeVO {
    private Long id;
    private Long sourceId;
    private Long targetId;
    private String relation;
}
