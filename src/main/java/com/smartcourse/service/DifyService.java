package com.smartcourse.service;

import com.smartcourse.pojo.dto.dify.DifyGradeShortQuestionDTO;
import com.smartcourse.pojo.dto.dify.base.streaming.DifyStreamEvent;
import com.smartcourse.pojo.dto.dify.exam.DifyExamGenQueryDTO;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

public interface DifyService {
    BigDecimal gradeShortQuestion(DifyGradeShortQuestionDTO dto);
    void mappingKnowledge(Long courseId,Long questionId,String question);
    Flux<DifyStreamEvent> processGenerateQuery(DifyExamGenQueryDTO dto);
}
