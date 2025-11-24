package com.smartcourse.pojo.vo.knowledge;

import lombok.Data;

@Data
public class NodeVO {
    private Long id;
    private Long externalId;
    private Long courseId;
    private String name;
    private String label;
    private String level;
    private Integer importance;
    private Long contentSize;
    private String description;
}
