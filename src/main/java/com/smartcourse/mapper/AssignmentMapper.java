package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.Assignment;
import com.smartcourse.pojo.vo.AssignmentVO;
import com.smartcourse.pojo.vo.teacher.assignment.TaskStudentListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AssignmentMapper {
    void insert(Assignment assignment);
    void update(Assignment assignment);
    Assignment selectById(Long id);

    java.util.List<Assignment> selectByTeacherAndCourse(Long creator, Long courseId);
    java.util.List<Assignment> selectByCourse(Long courseId);

    @Select("select name,description from evaluation_expert.assignment where id = #{assignmentId}")
    AssignmentVO getAssignment(Long assignmentId);

    List<TaskStudentListVO> getStudents(Integer assignmentId);
}