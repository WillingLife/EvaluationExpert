package com.smartcourse.converter;

import com.smartcourse.infra.redis.dto.SelectedQuestionItemDTO;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionConverter {
   default SelectedQuestionItemDTO questionQueryVoToSelectedItem(QuestionQueryVO vo){
       SelectedQuestionItemDTO dto = new SelectedQuestionItemDTO();
       dto.setId(vo.getId());
       dto.setStem(vo.getStem());
       dto.setDifficulty(vo.getDifficulty());
       if(vo.getType() == 1){
           dto.setType("single");
       }
       else if(vo.getType() == 2){
           dto.setType("multiple");
       }
       else if(vo.getType() == 3){
           dto.setType("fill_blank");
       }
       else if(vo.getType() == 4){
           dto.setType("short_answer");
       }
       else {
           dto.setType("unknown");
       }
       return dto;
   }

    List<SelectedQuestionItemDTO> questionQueryVosToSelectedItems(List<QuestionQueryVO> questionQueryVOs);
}
