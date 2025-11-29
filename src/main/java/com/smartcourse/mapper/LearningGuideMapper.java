package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.VideoProgress;
import com.smartcourse.pojo.vo.learn.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LearningGuideMapper {
    List<TaskCompletionSummaryVO> getAnalytics(String courseId);

    ExamStatisticsVO getExamStatistics(Integer studentId, Long courseId);

    StudentCoursePerformanceVO getPerformance(String studentId, String courseId);


    StudentTaskVO getTaskList(String studentId, Integer page, Integer size, String status);

    List<VideoProgress> getVideoMap(Long studentId, Long courseId);

    List<StudentVO> getStudents(Long classId);
}
