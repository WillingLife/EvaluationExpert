package com.smartcourse.service;

import com.smartcourse.pojo.vo.course.StudentCourseTaskVO;
import com.smartcourse.pojo.vo.course.StudentCourseVO;
import com.smartcourse.pojo.vo.course.TeacherCourseVO;

import java.util.List;

public interface CourseService {
    List<TeacherCourseVO> getCourseList(Long teacherId);

    List<StudentCourseVO> getCourseExam(Long studentId);

    List<StudentCourseTaskVO> getCourseTask(Long studentId);
}
