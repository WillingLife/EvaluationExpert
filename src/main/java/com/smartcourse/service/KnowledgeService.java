package com.smartcourse.service;

import com.smartcourse.pojo.dto.knowledge.*;
import com.smartcourse.pojo.vo.knowledge.*;

import java.util.List;

public interface KnowledgeService {
    StudentMapVO getStudentMap(StudentMapDTO studentMapDTO);

    MapVO getMap(Long courseId);

    ClassMapVO getClassMap(ClassMapDTO classMapDTO);

    CourseMapVO getCourseMap(Long examId);

    List<StudentMap> getStudent(StudentDTO studentDTO);

    List<ClassMap> getClazz(ClassDTO classDTO);

    List<CourseMap> getCourse(CourseDTO courseDTO);
}
