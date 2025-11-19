package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.AssignmentScore;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AssignmentScoreMapper {
    void insert(AssignmentScore score);
    void update(AssignmentScore score);
    AssignmentScore selectById(Long id);

    Integer selectMaxSubmitNo(Long assignmentId, Long studentId);
    List<AssignmentScore> selectByAssignmentAndStudent(Long assignmentId, Long studentId);

    @Delete("delete from evaluation_expert.assignment_score where assignment_id = #{assignmentId} and student_id = #{studentId}")
    void deleted(Long assignmentId, Long studentId);
}