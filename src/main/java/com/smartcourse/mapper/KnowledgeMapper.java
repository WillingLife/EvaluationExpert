package com.smartcourse.mapper;

import com.smartcourse.pojo.vo.knowledge.EdgeVO;
import com.smartcourse.pojo.vo.knowledge.ExamNodeVO;
import com.smartcourse.pojo.vo.knowledge.NodeVO;
import com.smartcourse.pojo.vo.knowledge.StudentScoreVO;
import jakarta.json.Json;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeMapper {
    @Select("select id, source_id, target_id, relation from evaluation_expert.knowledge_edge where course_id = #{courseId}")
    List<EdgeVO> getEdgeByCourseId(Long courseId);

    @Select("select * from evaluation_expert.knowledge_node where course_id = #{courseId}")
    List<NodeVO> getNodeByCourseId(Long courseId);

    @Insert("insert into evaluation_expert.`nodes-json`(student_id, exam_id, nodes) " +
            "values (#{studentId},#{examId},#{json})")
    void addNodes(Long examId, Long studentId, String json);

    @Select("select nodes from evaluation_expert.`nodes-json` where student_id = #{studentId} and exam_id = #{examId}")
    String getByJson(Long studentId, Long examId);

    List<Long> getClass(Long examId);

    @Select("select name from evaluation_expert.class where id = #{classId}")
    String getName(Long classId);

    @Select("select stem from evaluation_expert.question where id = #{questionId}")
    String getStem(Long questionId);

    @Select("select content from evaluation_expert.question_option where question_id = #{questionId}")
    List<String> getOptions(Long questionId);

    @Select("select difficulty from evaluation_expert.question where id = #{questionId}")
    Integer getDifficulty(Long questionId);

    @Select("select id, name from evaluation_expert.student where class_id = #{classId}")
    List<StudentScoreVO> getStudents(Long classId);

    List<Long> getCLazz(Long courseId);

    List<StudentScoreVO> getStudents2(List<Long> classIds);
}
