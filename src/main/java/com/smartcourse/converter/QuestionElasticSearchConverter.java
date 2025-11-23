package com.smartcourse.converter;

import com.smartcourse.pojo.dto.ElasticSearchKnowledgeDTO;
import com.smartcourse.pojo.dto.QuestionElasticSearchAddDTO;
import com.smartcourse.model.QuestionDocument;
import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.query.KnowledgeSearchQueryItem;
import com.smartcourse.pojo.query.QuestionElasticSearchQuery;
import com.smartcourse.pojo.query.QuestionKnowledgeSearchQuery;
import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionElasticSearchConverter {
    default QuestionDocument QuestionElasticSearchDTOToQuestionDocument(QuestionElasticSearchAddDTO questionElasticSearchAddDTO) {
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

    @Mapping(target = "from", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "queryVector", ignore = true)
    @Mapping(target = "vectorTopK", ignore = true)
    @Mapping(target = "vectorCandidates", ignore = true)
    QuestionElasticSearchQuery questionESQueryDTOToQuestionESQuery(QuestionElasticSearchQueryDTO questionElasticSearchQueryDTO);


    default QuestionKnowledgeSearchQuery questionESQueryDTOToKnowledgeQuery(QuestionElasticSearchQueryDTO  questionElasticSearchQueryDTO){
        QuestionKnowledgeSearchQuery query = new QuestionKnowledgeSearchQuery();
        query.setCourseId(questionElasticSearchQueryDTO.getCourseId());
        query.setQueryItems(knowledgeDTOToKnowledgeQueryItems(questionElasticSearchQueryDTO.getKnowledge()));
        return query;
    }



    KnowledgeSearchQueryItem knowledgeDTOToKnowledgeQueryItem(ElasticSearchKnowledgeDTO dto);

    List<KnowledgeSearchQueryItem> knowledgeDTOToKnowledgeQueryItems(List<ElasticSearchKnowledgeDTO> list);

    @Mapping(target = "vectorGet",constant = "false")
    @Mapping(target = "knowledgeGet",constant = "false")
    @Mapping(target = "cfGet",constant = "false")
    QuestionQueryESItemVO queryVoToESVo(QuestionQueryVO vo);


}
