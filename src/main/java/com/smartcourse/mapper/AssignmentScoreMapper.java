package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.AssignmentScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssignmentScoreMapper {
    void insert(AssignmentScore score);
    void update(AssignmentScore score);
    AssignmentScore selectById(Long id);

    Integer selectMaxSubmitNo(@Param("assignmentId") Long assignmentId,
                              @Param("studentId") Long studentId);
    List<AssignmentScore> selectByAssignmentAndStudent(@Param("assignmentId") Long assignmentId,
                                                       @Param("studentId") Long studentId);
}