package com.smartcourse.task;

import com.smartcourse.enums.ExamStatusEnum;
import com.smartcourse.mapper.ExamMapper;
import com.smartcourse.pojo.entity.Exam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExamStatusTask {

    private final ExamMapper examMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void updateExamStatus() {
        LocalDateTime now = LocalDateTime.now();
        updatePublishedToProgressing(now);
        updateProgressingToCompleted(now);
    }

    private void updatePublishedToProgressing(LocalDateTime now) {
        List<Exam> publishedExams = examMapper.selectByStatus(ExamStatusEnum.PUBLISHED.getValue());
        if (publishedExams == null || publishedExams.isEmpty()) {
            return;
        }
        for (Exam exam : publishedExams) {
            LocalDateTime startTime = exam.getStartTime();
            if (startTime == null || now.isBefore(startTime)) {
                continue;
            }
            examMapper.updateExamSelective(Exam.builder()
                    .id(exam.getId())
                    .status(ExamStatusEnum.PROGRESSING.getValue())
                    .build());
            log.info("Exam [{}] status updated to progressing.", exam.getId());
        }
    }

    private void updateProgressingToCompleted(LocalDateTime now) {
        List<Exam> progressingExams = examMapper.selectByStatus(ExamStatusEnum.PROGRESSING.getValue());
        if (progressingExams == null || progressingExams.isEmpty()) {
            return;
        }
        for (Exam exam : progressingExams) {
            LocalDateTime startTime = exam.getStartTime();
            Integer durationMinutes = exam.getDurationMinutes();
            if (startTime == null || durationMinutes == null) {
                continue;
            }
            LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
            if (now.isBefore(endTime)) {
                continue;
            }
            examMapper.updateExamSelective(Exam.builder()
                    .id(exam.getId())
                    .status(ExamStatusEnum.COMPLETED.getValue())
                    .build());
            log.info("Exam [{}] status updated to completed.", exam.getId());
        }
    }
}
