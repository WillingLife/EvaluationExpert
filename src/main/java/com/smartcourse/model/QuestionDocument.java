package com.smartcourse.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * 代表存储在Elasticsearch中的题目文档
 */
@Data
@Document(indexName = "question_bank")
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonProperty("question_text")
    private String questionText;

    /**
     * 题目文本的向量表示。
     */
    @Field(type = FieldType.Nested, name = "question_vector_chunks")
    @JsonProperty("question_vector_chunks")
    private List<QuestionVectorChunk> questionVectorChunks;

    /**
     * 分块向量类
     */
    @Data
    public static class QuestionVectorChunk {

        @Field(type = FieldType.Integer, name = "chunk_id")
        @JsonProperty("chunk_id")
        private Integer chunkId;

        @JsonProperty("chunk_text")
        @Field(type = FieldType.Text, name = "chunk_text")
        private String chunkText;

        /**
         * 题目分块向量（dims = 1024）
         */
        @JsonProperty("chunk_vector")
        @Field(type = FieldType.Dense_Vector, name = "chunk_vector")
        private float[] chunkVector;
    }


    /**
     * 答案文本。
     * 同样设置为Text类型，以便可以对答案内容进行搜索。
     */
    @JsonProperty("answer_text")
    @Field(type = FieldType.Text, name = "answer_text")
    private String answerText;

    /**
     * 答案文本的向量表示。
     * 可用于通过问题描述来搜索最相关的答案。
     */
    @JsonProperty("answer_vector_chunks")
    @Field(type = FieldType.Nested, name = "answer_vector_chunks")
    private List<AnswerVectorChunk> answerVectorChunks;

    @Data
    public static class AnswerVectorChunk {

        @JsonProperty("chunk_id")
        @Field(type = FieldType.Integer, name = "chunk_id")
        private Integer chunkId;

        @JsonProperty("chunk_text")
        @Field(type = FieldType.Text, name = "chunk_text")
        private String chunkText;

        /**
         * 答案分块向量（dims = 2048）
         */
        @JsonProperty("chunk_vector")
        @Field(type = FieldType.Dense_Vector, name = "chunk_vector")
        private float[] chunkVector;
    }

    /**
     * 所属课程的ID。
     * type = FieldType.Keyword 表示这个字段不分词，用于精确匹配、过滤和聚合。
     * 例如：WHERE course_id = 'CS101'
     */
    @JsonProperty("course_id")
    @Field(type = FieldType.Keyword, name = "course_id")
    private Long courseId;

    /**
     * 题目难度。
     * type = FieldType.Float 用于存储数值，便于进行范围查询和排序。
     */
    @JsonProperty("difficulty")
    @Field(type = FieldType.Float, name = "difficulty")
    private Float difficulty;

    /**
     * 作者的ID。
     * 同样设置为Keyword类型，用于精确过滤。
     */
    @JsonProperty("author_id")
    @Field(type = FieldType.Keyword, name = "author_id")
    private Long authorId;

    @JsonProperty("type")
    @Field(type = FieldType.Keyword,name = "type")
    private String type;



}
