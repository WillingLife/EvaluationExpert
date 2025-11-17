package com.smartcourse.converter;

import com.smartcourse.pojo.dto.QuestionElasticSearchAddDTO;
import com.smartcourse.model.QuestionDocument;

public interface QuestionElasticSearchConverter {
    QuestionDocument QuestionElasticSearchDTOToQuestionDocument(QuestionElasticSearchAddDTO questionElasticSearchAddDTO);
}
