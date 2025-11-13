package com.smartcourse.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * 知识点节点实体，对应表 `knowledge_node`。
 * 说明：用于题目与课程知识体系的关联。
 */
public class KnowledgeNode {
    private Long id;             // 主键ID
    private Long externalId;     // 外部系统ID（可选，用于对接）
    private Long courseId;       // 课程ID
    private String name;         // 知识点名称
}