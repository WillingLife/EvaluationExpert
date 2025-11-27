package com.smartcourse.service;

import com.smartcourse.mapper.QuestionMapper;
import com.smartcourse.pojo.vo.exam.question.StudentExamChoiceQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamFillBlankQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamShortAnswerQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Async("taskExecutor")
    public CompletableFuture<List<StudentExamChoiceQuestionVO>> getChoiceAsync(List<Long> choiceQuestionIds, Long examId) {
        if (choiceQuestionIds == null || choiceQuestionIds.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(questionMapper.getChoice(choiceQuestionIds,examId));
    }

    @Async("taskExecutor")
    public CompletableFuture<List<StudentExamFillBlankQuestionVO>> getFillAsync(List<Long> fillBlankIds, Long examId) {
        if (fillBlankIds == null || fillBlankIds.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(questionMapper.getFill(fillBlankIds,examId));
    }

    @Async("taskExecutor")
    public CompletableFuture<List<StudentExamShortAnswerQuestionVO>> getShortAsync(List<Long> shortanswerIds, Long examId) {
        if (shortanswerIds == null || shortanswerIds.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(questionMapper.getShort(shortanswerIds,examId));
    }
}
