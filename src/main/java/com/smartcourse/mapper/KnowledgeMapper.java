package com.smartcourse.mapper;

import com.smartcourse.pojo.vo.knowledge.EdgeVO;
import com.smartcourse.pojo.vo.knowledge.NodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeMapper {
    @Select("select id, source_id, target_id, relation from evaluation_expert.knowledge_edge where course_id = #{courseId}")
    List<EdgeVO> getEdgeByCourseId(Long courseId);

    @Select("select * from evaluation_expert.knowledge_node where course_id = #{courseId}")
    List<NodeVO> getNodeByCourseId(Long courseId);
}
