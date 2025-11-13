package com.smartcourse.evaluationexpert.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * 代表存储在Elasticsearch中的题目文档
 */
@Document(indexName = "question_bank")
@Data
public class QuestionDocument {
    /**
     * 文档的唯一ID,使用数据库中的主键。
     */
    @Id
    private Long id;

    /**
     * 题目文本。
     * type = FieldType.Text 表示这个字段是全文检索字段，会被分词器处理，适用于模糊搜索。
     */
    @Field(type = FieldType.Text, name = "question_text")
    private String questionText;

    /**
     * 题目文本的向量表示。
     * type = FieldType.Dense_Vector 是专门用于向量搜索的类型，对于实现“以题搜题”至关重要。
     * 注意：在创建索引时，需要为这个字段指定维度（dims）。
     */
    @Field(type = FieldType.Dense_Vector, name = "question_vector")
    private List<Double> questionVector;

    /**
     * 答案文本。
     * 同样设置为Text类型，以便可以对答案内容进行搜索。
     */
    @Field(type = FieldType.Text, name = "answer_text")
    private String answerText;

    /**
     * 答案文本的向量表示。
     * 可用于通过问题描述来搜索最相关的答案。
     */
    @Field(type = FieldType.Dense_Vector, name = "answer_vector")
    private List<Double> answerVector;

    /**
     * 所属课程的ID。
     * type = FieldType.Keyword 表示这个字段不分词，用于精确匹配、过滤和聚合。
     * 例如：WHERE course_id = 'CS101'
     */
    @Field(type = FieldType.Keyword, name = "course_id")
    private String courseId;

    /**
     * 题目难度。
     * type = FieldType.Integer 用于存储数值，便于进行范围查询和排序。
     * 例如：难度介于 3 到 5 之间。
     */
    @Field(type = FieldType.Integer, name = "difficulty")
    private Integer difficulty;

    /**
     * 作者的ID。
     * 同样设置为Keyword类型，用于精确过滤。
     */
    @Field(type = FieldType.Keyword, name = "author_id")
    private Long authorId;

}
