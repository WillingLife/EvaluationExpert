package com.smartcourse.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Document(indexName = "question_knowledge")
public class QuestionKnowledgeDocument {
    @Id
    private Long id;  // question 的数据库 ID

    @Field(type = FieldType.Nested,name = "knowledge_points")
    private List<KnowledgePoint> knowledgePoints;

    @Field(type = FieldType.Keyword,name = "course_id")
    private Long courseId;
}
