package com.smartcourse.service;

import com.smartcourse.pojo.vo.course.*;

import java.util.List;

public interface CourseService {
    List<TeacherCourseVO> getCourseList(Long teacherId);

    List<StudentCourseVO> getCourseExam(Long studentId);

    List<StudentCourseTaskVO> getCourseTask(Long studentId);

    List<CourseVO> getListAll(Long teacherId);

    List<ExamListVO> getExamList(Long courseId);

    List<ExamStudentVO> getStudents(Long examId, Long classId);

    List<ExamVO> getExam(Long courseId);

    List<String> getTaskFile(Long assignmentId);

    List<String> getTaskName(Long assignmentId);
}
