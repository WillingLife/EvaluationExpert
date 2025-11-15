package com.smartcourse.pojo.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionUpdateDTO {
    private Long id;
    private Integer type;
    private Long courseId;
    private String stem;
    private String analysis;
    private Integer difficulty;
    private Long teacherId;
    private Integer active;
    private Integer deleted;

    private JsonNode details;
}