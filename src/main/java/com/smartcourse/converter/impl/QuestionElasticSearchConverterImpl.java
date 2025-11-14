package com.smartcourse.converter.impl;

import com.smartcourse.converter.QuestionElasticSearchConverter;
import com.smartcourse.pojo.dto.QuestionElasticSearchDTO;
import com.smartcourse.model.QuestionDocument;
import org.springframework.stereotype.Component;


@Component
public class QuestionElasticSearchConverterImpl implements QuestionElasticSearchConverter {
    @Override
    public QuestionDocument QuestionElasticSearchDTOToQuestionDocument(QuestionElasticSearchDTO questionElasticSearchDTO) {
        QuestionDocument questionDocument = new QuestionDocument();
        questionDocument.setId(questionElasticSearchDTO.getId());
        questionDocument.setQuestionText(questionElasticSearchDTO.getQuestionText());
        questionDocument.setAnswerText(questionElasticSearchDTO.getAnswerText());
        questionDocument.setDifficulty(questionElasticSearchDTO.getDifficulty());
        questionDocument.setCourseId(questionElasticSearchDTO.getCourseId());
        questionDocument.setAuthorId(questionElasticSearchDTO.getAuthorId());
        return questionDocument;
    }
}
