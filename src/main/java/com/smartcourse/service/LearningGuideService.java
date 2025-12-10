package com.smartcourse.service;

import com.smartcourse.pojo.vo.learn.*;

import java.util.List;

public interface LearningGuideService {
    List<TaskCompletionSummaryVO> getAnalytics(String courseId);

    ExamStatisticsVO getExamStatistics(Integer studentId, Long courseId);

    StudentCoursePerformanceVO getPerformance(String studentId, String courseId);

    List<KnowledgePointPerformanceVO> getPointsPerformance(String courseId, Long studentId);

    StudentTaskVO getTaskList(String studentId, Integer page, Integer size, String status);

    List<VideoProgressVO> getVideoMap(Long studentId, Long courseId);

    List<ClassVideoVO> getClassVideo(Long classId, Long courseId);
}
