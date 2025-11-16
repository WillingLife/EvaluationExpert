package com.smartcourse.service.impl;

import com.smartcourse.converter.ExamConverter;
import com.smartcourse.converter.ExamSectionConverter;
import com.smartcourse.mapper.ExamItemMapper;
import com.smartcourse.mapper.ExamMapper;
import com.smartcourse.mapper.ExamSectionMapper;
import com.smartcourse.pojo.dto.TeacherSaveExamDTO;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherExamServiceImpl implements TeacherExamService {

    private final ExamConverter examConverter;
    private final ExamMapper examMapper;
    private final ExamSectionMapper examSectionMapper;
    private final ExamItemMapper  examItemMapper;

    @Override
    @Transactional
    public Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO) {
        Exam exam = examConverter.teacherSaveExamDTOToExam(teacherSaveExamDTO);
        if(exam.getId()==null){
           int insertExam = examMapper.insertExam(exam);
           exam.batchUpdateExamIdIntoSections();
           examSectionMapper.insetSections(exam.getSections());
           exam.batchUpdateExamIdIntoSections();
           examItemMapper.insertExamItemsByExamSections(exam.getSections());
        }else {
            // TODO 实现更新操作

        }




        return 0L;
    }
}
