package com.smartcourse.pojo.vo.exam.question;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentExamQuestionVO {
    private Long questionId;
    private String questionStem;
    private Integer score;

    private JsonNode details;          // 根据传入的json字符串解析对象
}
