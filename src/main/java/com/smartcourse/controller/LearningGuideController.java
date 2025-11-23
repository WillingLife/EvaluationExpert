package com.smartcourse.controller;

import com.smartcourse.pojo.vo.learn.*;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.LearningGuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin
public class LearningGuideController {
    @Autowired
    private LearningGuideService learningGuideService;

    @GetMapping("/tasks/student/{studentId}")
    public StudentTaskVO taskList(Integer page, Integer size, String status, @PathVariable String studentId) {
        return learningGuideService.getTaskList(studentId, page, size, status);
    }

    @GetMapping("/analytics/courses/{courseId}/task-completion-summary")
    public List<TaskCompletionSummaryVO> getAnalytics(@PathVariable String courseId) {
        return learningGuideService.getAnalytics(courseId);
    }

    @GetMapping("/students/{studentId}/exam-statistics")
    public ExamStatisticsVO getExamStatistics(Long courseId, @PathVariable Integer studentId) {
        return learningGuideService.getExamStatistics(studentId, courseId);
    }

    @GetMapping("/analytics/student/{studentId}/course/{courseId}/performance-overview")
    public StudentCoursePerformanceVO getPerformance(@PathVariable String courseId, @PathVariable String studentId) {
        return learningGuideService.getPerformance(studentId, courseId);
    }

    @GetMapping("/analytics/courses/{courseId}/knowledge-points/performance")
    public List<KnowledgePointPerformanceVO> getPointsPerformance(@PathVariable String courseId, Long studentId) {
        return learningGuideService.getPointsPerformance(courseId, studentId);
    }
}
