package com.smartcourse.mapper;

import com.smartcourse.pojo.vo.course.StudentCourseTaskVO;
import com.smartcourse.pojo.vo.course.StudentCourseVO;
import com.smartcourse.pojo.vo.course.TeacherCourseVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper {
    List<TeacherCourseVO> getCourseList(Long teacherId);

    List<StudentCourseVO> getCourseExam(Long studentId);

    List<StudentCourseTaskVO> getCourseTask(Long studentId);
}
