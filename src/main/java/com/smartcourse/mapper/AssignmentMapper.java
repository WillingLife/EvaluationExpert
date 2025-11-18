package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.Assignment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AssignmentMapper {
    void insert(Assignment assignment);
    void update(Assignment assignment);
    Assignment selectById(Long id);

    java.util.List<Assignment> selectByTeacherAndCourse(Long creator, Long courseId);
    java.util.List<Assignment> selectByCourse(Long courseId);
}