package com.smartcourse.converter;

import com.smartcourse.pojo.dto.exam.TeacherSaveExamQuestionDTO;
import com.smartcourse.pojo.entity.ExamItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExamItemConverter {
  default ExamItem teacherSaveExamQuestionDTOToExamItem(TeacherSaveExamQuestionDTO dto,Integer orderNo,Long sectionId) {
      ExamItem examItem = new ExamItem();
      examItem.setId(dto.getExamItemId());
      examItem.setQuestionId(dto.getQuestionId());
      examItem.setOrderNo(orderNo);
      examItem.setScore(dto.getScore());
      examItem.setSectionId(sectionId);
      return examItem;
  }

  default List<ExamItem> teacherQuestionDTOsTOExamItems(List<TeacherSaveExamQuestionDTO> list,Long sectionId) {
      List<ExamItem> examItems = new ArrayList<>();
      int i=1;
      for(TeacherSaveExamQuestionDTO dto : list) {
          examItems.add(teacherSaveExamQuestionDTOToExamItem(dto,i,sectionId));
          i++;
      }
      return examItems;
  }
}
