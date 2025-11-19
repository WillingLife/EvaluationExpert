package com.smartcourse.service;

import com.smartcourse.pojo.dto.dify.DifyGradeShortQuestionDTO;

import java.math.BigDecimal;

public interface DifyService {
    BigDecimal gradeShortQuestion(DifyGradeShortQuestionDTO dto);
}
