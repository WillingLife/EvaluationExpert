package com.smartcourse.converter;

import com.smartcourse.enums.ExamStatusEnum;
import com.smartcourse.pojo.dto.TeacherSaveExamDTO;
import com.smartcourse.pojo.entity.Exam;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",uses = {ExamSectionConverter.class})
public abstract class ExamConverter {
    @Autowired
    private ExamSectionConverter examSectionConverter;


    public Exam teacherSaveExamDTOToExam(TeacherSaveExamDTO dto){
       Exam exam = new Exam();
       exam.setId(dto.getExamId());
       exam.setName(dto.getExamName());
       exam.setDescription(dto.getDescription());
       exam.setNotice(dto.getExamNotice());
       exam.setCourseId(dto.getCourseId());
       exam.setTotalScore(dto.getTotalScore());
       exam.setDurationMinutes(dto.getDurationMinutes());
       exam.setStartTime(dto.getStartTime());
       exam.setPassScore(dto.getPassScore());
       exam.setShuffleQuestions(dto.getShuffleQuestions());
       exam.setShuffleOptions(dto.getShuffleOptions());
       exam.setStatus(ExamStatusEnum.DRAFT.getValue());
       exam.setCreator(dto.getTeacherId());
       exam.setDeleted(false);
       exam.setSections(examSectionConverter.teacherSaveExamSectionListToExamSectionList(dto.getSections(),
               dto.getExamId(), dto.getTeacherId()));
       return exam;

   }

}
