package com.smartcourse.infra.ai.vo;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonClassDescription("AI返回的字段")
public class ExamGenCriteria {
    @JsonPropertyDescription("AI选择的题目id列表，id为长整型")
    private List<Long> ids;
    @JsonPropertyDescription("AI总结之前回答和本次回答形成的上下文")
    private String context;
}
