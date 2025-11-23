package com.smartcourse.service.impl;

import com.smartcourse.mapper.LearningGuideMapper;
import com.smartcourse.pojo.vo.learn.*;
import com.smartcourse.service.LearningGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningGuideServiceImpl implements LearningGuideService {
    @Autowired
    private LearningGuideMapper learningGuideMapper;

    @Override
    public List<TaskCompletionSummaryVO> getAnalytics(String courseId) {
        return learningGuideMapper.getAnalytics(courseId);
    }

    @Override
    public ExamStatisticsVO getExamStatistics(Integer studentId, Long courseId) {
        return learningGuideMapper.getExamStatistics(studentId, courseId);
    }

    @Override
    public StudentCoursePerformanceVO getPerformance(String studentId, String courseId) {
        return learningGuideMapper.getPerformance(studentId, courseId);
    }

    @Override
    public List<KnowledgePointPerformanceVO> getPointsPerformance(String courseId, Long studentId) {
        //TODO
        return null;
    }

    @Override
    public StudentTaskVO getTaskList(String studentId, Integer page, Integer size, String status) {
        return learningGuideMapper.getTaskList(studentId, page, size, status);
    }
}
