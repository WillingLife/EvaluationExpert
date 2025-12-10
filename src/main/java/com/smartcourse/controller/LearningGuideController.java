package com.smartcourse.controller;

import com.smartcourse.pojo.entity.VideoProgress;
import com.smartcourse.pojo.vo.learn.*;
import com.smartcourse.result.compat.Result;
import com.smartcourse.service.LearningGuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Learning Guide", description = "学习任务与知识点表现相关接口")
public class LearningGuideController {
    @Autowired
    private LearningGuideService learningGuideService;

    @GetMapping("/tasks/student/{studentId}")
    @Operation(summary = "查询学生任务列表", description = "按照分页条件检索学生的学习任务")
    public StudentTaskVO taskList(
            @Parameter(description = "页码，从1开始", example = "1") Integer page,
            @Parameter(description = "每页记录数", example = "10") Integer size,
            @Parameter(description = "任务状态（如todo、done）") String status,
            @Parameter(description = "学生ID", required = true) @PathVariable String studentId) {
        return learningGuideService.getTaskList(studentId, page, size, status);
    }

    @GetMapping("/analytics/courses/{courseId}/task-completion-summary")
    @Operation(summary = "课程任务完成情况概览", description = "返回指定课程的任务完成统计信息")
    public List<TaskCompletionSummaryVO> getAnalytics(
            @Parameter(description = "课程ID", required = true) @PathVariable String courseId) {
        return learningGuideService.getAnalytics(courseId);
    }

    @GetMapping("/students/{studentId}/exam-statistics")
    @Operation(summary = "学生考试统计", description = "查询学生在某课程下的考试得分表现")
    public ExamStatisticsVO getExamStatistics(
            @Parameter(description = "课程ID", required = true) Long courseId,
            @Parameter(description = "学生ID", required = true) @PathVariable Integer studentId) {
        return learningGuideService.getExamStatistics(studentId, courseId);
    }

    @GetMapping("/analytics/student/{studentId}/course/{courseId}/performance-overview")
    @Operation(summary = "学生课程表现概览", description = "汇总学生在指定课程中的表现和掌握情况")
    public StudentCoursePerformanceVO getPerformance(
            @Parameter(description = "课程ID", required = true) @PathVariable String courseId,
            @Parameter(description = "学生ID", required = true) @PathVariable String studentId) {
        return learningGuideService.getPerformance(studentId, courseId);
    }

    @GetMapping("/analytics/courses/{courseId}/knowledge-points/performance")
    @Operation(summary = "知识点掌握情况", description = "针对课程下的知识点输出学生掌握度")
    public List<KnowledgePointPerformanceVO> getPointsPerformance(
            @Parameter(description = "课程ID", required = true) @PathVariable String courseId,
            @Parameter(description = "学生ID，可选") Long studentId) {
        return learningGuideService.getPointsPerformance(courseId, studentId);
    }


    @GetMapping("/video-map")
    public Result<List<VideoProgressVO>> videoMap(@RequestParam("student_id") Long studentId, @RequestParam("course_id") Long courseId) {
        List<VideoProgressVO> videoProgress = learningGuideService.getVideoMap(studentId, courseId);
        return Result.success(videoProgress);
    }

    @GetMapping("class/video-map")
    public Result<List<ClassVideoVO>> classVideoMap(@RequestParam("class_id") Long classId, @RequestParam("course_id") Long courseId) {
        List<ClassVideoVO> classVideoVO = learningGuideService.getClassVideo(classId, courseId);
        return Result.success(classVideoVO);
    }
}
