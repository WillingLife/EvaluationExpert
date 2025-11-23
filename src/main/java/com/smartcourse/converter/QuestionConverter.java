package com.smartcourse.converter;

import com.smartcourse.infra.redis.dto.SelectedQuestionItemDTO;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionConverter {
    SelectedQuestionItemDTO questionQueryVoToSelectedItem(QuestionQueryVO questionQueryVO);

    List<SelectedQuestionItemDTO> questionQueryVosToSelectedItems(List<QuestionQueryVO> questionQueryVOs);
}
