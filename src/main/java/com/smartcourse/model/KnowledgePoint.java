package com.smartcourse.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class KnowledgePoint {
    @Field(type = FieldType.Long,name = "id")
    private Long id;

    @Field(type = FieldType.Double,name = "weight")
    private Double weight;
}
