package com.smartcourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.mapper.LearningGuideMapper;
import com.smartcourse.pojo.dto.ProgressDTO;
import com.smartcourse.pojo.entity.VideoProgress;
import com.smartcourse.pojo.vo.learn.*;
import com.smartcourse.service.LearningGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public List<VideoProgressVO> getVideoMap(Long studentId, Long courseId) {
        List<VideoProgress> videoProgressS = learningGuideMapper.getVideoMap(studentId, courseId);
        List<VideoProgressVO> videoProgressVOS = new ArrayList<VideoProgressVO>();
        for (VideoProgress videoProgress : videoProgressS) {
            String progress = videoProgress.getProgress();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ProgressDTO progressDTO = objectMapper.readValue(progress, ProgressDTO.class);
                VideoProgressVO videoProgressVO = new VideoProgressVO();
                videoProgressVO.setProgress(progressDTO);
                videoProgressVO.setId(videoProgress.getId());
                videoProgressVO.setCompletion(videoProgress.getCompletion());
                videoProgressVO.setStudentId(videoProgress.getStudentId());
                videoProgressVO.setResourceId(videoProgress.getResourceId());
                videoProgressVO.setLastViewTime(videoProgress.getLastViewTime());
                videoProgressVOS.add(videoProgressVO);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return videoProgressVOS;
    }

    @Override
    public List<ClassVideoVO> getClassVideo(Long classId, Long courseId) {
        List<ClassVideoVO> classVideoVOS = new ArrayList<>();

        List<StudentVO> studentVOS = learningGuideMapper.getStudents(classId);
        for (StudentVO studentVO : studentVOS) {
            ClassVideoVO classVideoVO = new ClassVideoVO();
            classVideoVO.setStudentId(studentVO.getId());
            classVideoVO.setStudentName(studentVO.getName());
            List<VideoProgressVO> videoProgressVOS = getVideoMap(studentVO.getId(), courseId);
            classVideoVO.setVideoProgressVOList(videoProgressVOS);
            classVideoVOS.add(classVideoVO);
        }

        return classVideoVOS;
    }
}
