package com.smartcourse.service.impl;

import com.smartcourse.mapper.CourseMapper;
import com.smartcourse.pojo.vo.course.StudentCourseTaskVO;
import com.smartcourse.pojo.vo.course.StudentCourseVO;
import com.smartcourse.pojo.vo.course.TeacherCourseVO;
import com.smartcourse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<TeacherCourseVO> getCourseList(Long teacherId) {
        return courseMapper.getCourseList(teacherId);
    }

    @Override
    public List<StudentCourseVO> getCourseExam(Long studentId) {
        return courseMapper.getCourseExam(studentId);
    }

    @Override
    public List<StudentCourseTaskVO> getCourseTask(Long studentId) {
        return courseMapper.getCourseTask(studentId);
    }
}
