package com.smartcourse.converter;

import com.smartcourse.pojo.dto.TeacherGradeItemDTO;
import com.smartcourse.pojo.entity.ExamScoreItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExamScoreItemConverter {
   default ExamScoreItem teacherGradeItemDTOToExamScoreItem(TeacherGradeItemDTO dto,Long scoreId){
       ExamScoreItem res = new ExamScoreItem();
       res.setScoreId(scoreId);
       res.setExamItemId(dto.getExamItemId());
       res.setScore(dto.getScore());
       res.setRemark(dto.getRemark());
       res.setGradeTime(LocalDateTime.now());
       return res;
   }

   default List<ExamScoreItem> teacherGradeItemsToExamScoreItems(List<TeacherGradeItemDTO> dtos,Long scoreId){
       List<ExamScoreItem> res = new ArrayList<>();
       for(TeacherGradeItemDTO dto : dtos){
           res.add(teacherGradeItemDTOToExamScoreItem(dto,scoreId));
       }
       return res;
   }
}
