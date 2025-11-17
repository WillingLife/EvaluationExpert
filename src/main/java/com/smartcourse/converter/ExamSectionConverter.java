package com.smartcourse.converter;

import com.smartcourse.pojo.dto.exam.TeacherSaveExamSectionDTO;
import com.smartcourse.pojo.entity.ExamItem;
import com.smartcourse.pojo.entity.ExamSection;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",uses = {ExamItemConverter.class})
public abstract class ExamSectionConverter {

    @Autowired
    private ExamItemConverter examItemConverter;

   public ExamSection teacherSaveExamSectionToExamSection(TeacherSaveExamSectionDTO dto,Long examId,Long creator){
       ExamSection examSection = new ExamSection();
       examSection.setId(dto.getSectionId());
       examSection.setExamId(examId);
       examSection.setTitle(dto.getTitle());
       examSection.setQuestionType(dto.getQuestionType().getValue());
       examSection.setChoiceScore(dto.getChoiceScore());
       examSection.setOrderNo(dto.getOrderNo());
       examSection.setChoiceNegativeScore(dto.getScoreNegativeScore());
       examSection.setMultipleStrategy(dto.getMultipleStrategy());
       examSection.setMultipleStrategyConf(dto.getMultipleStrategyConf());
       examSection.setCreator(creator);
       examSection.setDeleted(false);
       List<ExamItem> examItems =examItemConverter.teacherQuestionDTOsTOExamItems(dto.getQuestions(),dto.getSectionId());
       examSection.setExamItems(examItems);
       return examSection;
   }


  public List<ExamSection> teacherSaveExamSectionListToExamSectionList(List<TeacherSaveExamSectionDTO> dtoList,
                                                                                     Long examId, Long creator) {
      List<ExamSection> list = new ArrayList<>();
      for (TeacherSaveExamSectionDTO dto : dtoList) {
          list.add(teacherSaveExamSectionToExamSection(dto,examId,creator));
      }
      return list;
  }

}
