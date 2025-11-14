package com.smartcourse.converter;

import com.smartcourse.pojo.dto.QuestionElasticSearchDTO;
import com.smartcourse.model.QuestionDocument;

public interface QuestionElasticSearchConverter {
    QuestionDocument QuestionElasticSearchDTOToQuestionDocument(QuestionElasticSearchDTO questionElasticSearchDTO);
}
