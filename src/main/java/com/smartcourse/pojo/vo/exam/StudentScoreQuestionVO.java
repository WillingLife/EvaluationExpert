package com.smartcourse.pojo.vo.exam;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentScoreQuestionVO {
    private Long questionId;
    private Long fullScore;
    private Long studentScore;
    private String questionStem;
    private String answer;
    private Object studentAnswer;
    private Object correctAnswer;

    // NON_NULL: null 时不序列化
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Option> options;

    // NON_NULL: null 时不序列化
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer blankCount;

    // NON_EMPTY: null 或空字符串时都不序列化
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String remark;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Option {
        private Long optionId;
        private String content;
    }
}
