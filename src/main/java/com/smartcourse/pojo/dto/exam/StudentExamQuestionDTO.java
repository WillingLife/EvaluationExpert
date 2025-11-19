package com.smartcourse.pojo.dto.exam;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StudentExamQuestionDTO {
    private Long questionId;

    // 学生答案：可以是数组（单选、多选、填空）或字符串（简答）
    private Object studentAnswer;

    /**
     * 获取单选/多选答案（选项ID列表）
     */
    @SuppressWarnings("unchecked")
    public List<Long> getChoiceAnswer() {
        if (studentAnswer instanceof List) {
            return (List<Long>) studentAnswer;
        }
        return null;
    }

    /**
     * 获取填空题答案（字符串列表）
     */
    @SuppressWarnings("unchecked")
    public List<String> getFillBlankAnswer() {
        if (studentAnswer instanceof List) {
            return (List<String>) studentAnswer;
        }
        return null;
    }

    /**
     * 获取简答题答案（字符串）
     */
    public String getShortAnswer() {
        if (studentAnswer instanceof String) {
            return (String) studentAnswer;
        }
        return null;
    }
}
