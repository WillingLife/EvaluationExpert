package com.smartcourse.mapper;

import com.smartcourse.pojo.vo.learn.ExamStatisticsVO;
import com.smartcourse.pojo.vo.learn.StudentCoursePerformanceVO;
import com.smartcourse.pojo.vo.learn.StudentTaskVO;
import com.smartcourse.pojo.vo.learn.TaskCompletionSummaryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LearningGuideMapper {
    List<TaskCompletionSummaryVO> getAnalytics(String courseId);

    ExamStatisticsVO getExamStatistics(Integer studentId, Long courseId);

    StudentCoursePerformanceVO getPerformance(String studentId, String courseId);


    StudentTaskVO getTaskList(String studentId, Integer page, Integer size, String status);
}
