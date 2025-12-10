package com.smartcourse.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionElasticSearchAddDTO {
    /**
     * 文档的唯一ID,使用数据库中的主键。
     */
    private Long id;

    /**
     * 题目文本。
     */
    private String questionText;

    /**
     * 答案文本,选择题为空
     */
    private String answerText;

    /**
     * 所属课程的ID。
     */
    private Long courseId;

    /**
     * 题目难度。
     */
    private Float difficulty;

    /**
     * 作者的ID。
     */
    private Long authorId;

    /**
     * 题目类型
     */
    private String type;

}
