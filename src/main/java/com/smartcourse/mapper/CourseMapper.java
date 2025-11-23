package com.smartcourse.mapper;

import com.smartcourse.pojo.vo.course.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper {
    List<TeacherCourseVO> getCourseList(Long teacherId);

    List<StudentCourseVO> getCourseExam(Long studentId);

    List<StudentCourseTaskVO> getCourseTask(Long studentId);

    List<CourseVO> getListAll(Long teacherId);

    List<ExamListVO> getExamList(Long courseId);

    List<ExamStudentVO> getStudents(Long examId, Long classId);

    List<ExamVO> getExam(Long courseId);
}
