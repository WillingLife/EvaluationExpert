package com.smartcourse.service.impl;

import com.smartcourse.mapper.CourseMapper;
import com.smartcourse.pojo.vo.course.*;
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

    @Override
    public List<CourseVO> getListAll(Long teacherId) {
        return courseMapper.getListAll(teacherId);
    }

    @Override
    public List<ExamListVO> getExamList(Long courseId) {
        return courseMapper.getExamList(courseId);
    }

    @Override
    public List<ExamStudentVO> getStudents(Long examId, Long classId) {
        return courseMapper.getStudents(examId, classId);
    }

    @Override
    public List<ExamVO> getExam(Long courseId) {
        return courseMapper.getExam(courseId);
    }
}
