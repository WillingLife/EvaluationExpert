package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.KnowledgeNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeNodeMapper {

    @Select("SELECT id, external_id AS externalId, course_id AS courseId, name " +
            "FROM evaluation_expert.knowledge_node WHERE course_id = #{courseId}")
    List<KnowledgeNode> getByCourseId(Long courseId);
}
