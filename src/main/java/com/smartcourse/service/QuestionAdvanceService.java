package com.smartcourse.service;

import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.vo.question.QuestionQueryESVO;

public interface QuestionAdvanceService {
    QuestionQueryESVO queryAdvance(QuestionElasticSearchQueryDTO dto);
}
