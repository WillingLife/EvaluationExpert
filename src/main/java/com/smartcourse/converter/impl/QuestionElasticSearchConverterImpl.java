package com.smartcourse.converter.impl;

import com.smartcourse.converter.QuestionElasticSearchConverter;
import com.smartcourse.pojo.dto.QuestionElasticSearchAddDTO;
import com.smartcourse.model.QuestionDocument;
import org.springframework.stereotype.Component;


@Component
public class QuestionElasticSearchConverterImpl implements QuestionElasticSearchConverter {
    @Override
    public QuestionDocument QuestionElasticSearchDTOToQuestionDocument(QuestionElasticSearchAddDTO questionElasticSearchAddDTO) {
        QuestionDocument questionDocument = new QuestionDocument();
        questionDocument.setId(questionElasticSearchAddDTO.getId());
        questionDocument.setQuestionText(questionElasticSearchAddDTO.getQuestionText());
        questionDocument.setAnswerText(questionElasticSearchAddDTO.getAnswerText());
        questionDocument.setDifficulty(questionElasticSearchAddDTO.getDifficulty());
        questionDocument.setCourseId(questionElasticSearchAddDTO.getCourseId());
        questionDocument.setAuthorId(questionElasticSearchAddDTO.getAuthorId());
        questionDocument.setType(questionElasticSearchAddDTO.getType());
        return questionDocument;
    }
}
