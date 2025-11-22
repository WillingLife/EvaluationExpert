package com.smartcourse.service;

import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;

import java.util.List;

public interface ElasticSearchQueryService {

    List<QuestionQueryESItemVO> queryQuestionDocument(QuestionElasticSearchQueryDTO dto);
}
