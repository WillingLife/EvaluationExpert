package com.smartcourse.converter;


import com.smartcourse.infra.ai.dto.AiQueryQuestionDTO;
import com.smartcourse.infra.ai.vo.AiQueryQuestionVOItem;
import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AiToolConverter {
    @Mapping(target = "synonymy",constant = "false")
    @Mapping(target = "fuzzy",constant = "false")
    @Mapping(target = "searchAnswer",constant = "false")
    @Mapping(target = "useVector",constant = "true")
    @Mapping(target = "authorId",ignore = true)
    @Mapping(target = "reranker",ignore = true)
    @Mapping(target = "pageNumber",ignore = true)
    QuestionElasticSearchQueryDTO aiQueryDtoToEsQueryDto(AiQueryQuestionDTO aiQueryQuestionDTO);

   default AiQueryQuestionVOItem esQueryVoToAiQueryVo(QuestionQueryESItemVO vo){
       AiQueryQuestionVOItem res = new AiQueryQuestionVOItem();
       res.setId(vo.getId());
       res.setQuestion(vo.getStem());
       res.setDifficulty(vo.getDifficulty());
       Integer type = vo.getType();
       if(type == null){
           return res;
       }
       if(type==1){
           res.setType("single");
       }
       if(type==2){
           res.setType("multiple");
       }
       if (type==3){
           res.setType("fill_blank");
       }
       if (type==4){
           res.setType("short_answer");
       }
       return res;
   }

    List<AiQueryQuestionVOItem> esQueryVoListToAiQueryVoLIst(List<QuestionQueryESItemVO> vos);
}
