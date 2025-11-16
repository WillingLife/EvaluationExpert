package com.smartcourse.service.impl;

import com.smartcourse.converter.ExamConverter;
import com.smartcourse.enums.ExamStatusEnum;
import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.exception.SqlErrorException;
import com.smartcourse.mapper.ExamClassMapper;
import com.smartcourse.mapper.ExamItemMapper;
import com.smartcourse.mapper.ExamMapper;
import com.smartcourse.mapper.ExamSectionMapper;
import com.smartcourse.pojo.dto.TeacherGradeDTO;
import com.smartcourse.pojo.dto.TeacherPublishExamDTO;
import com.smartcourse.pojo.dto.TeacherSaveExamDTO;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TeacherExamServiceImpl implements TeacherExamService {

    private final ExamConverter examConverter;
    private final ExamMapper examMapper;
    private final ExamSectionMapper examSectionMapper;
    private final ExamItemMapper examItemMapper;
    private final ExamClassMapper examClassMapper;

    @Override
    @Transactional
    public Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO) {
        Exam exam = examConverter.teacherSaveExamDTOToExam(teacherSaveExamDTO);
        if (exam.getId() == null) {
            if(examMapper.insertExam(exam)<1){
                throw new SqlErrorException("数据库操作失败");
            }
            exam.batchUpdateExamIdIntoSections();
            examSectionMapper.insetSections(exam.getSections());
            exam.batchUpdateExamIdIntoSections();
            examItemMapper.insertExamItemsByExamSections(exam.getSections());
        } else {
            examMapper.updateExamRecursive(exam);
        }


        return 0L;
    }

    @Override
    @Transactional
    public void publishExam(TeacherPublishExamDTO teacherPublishExamDTO) {
        Exam exam = examMapper.getById(teacherPublishExamDTO.getExam_id());
        if (exam == null || !Objects.equals(exam.getStatus(), ExamStatusEnum.DRAFT.getValue())) {
            throw new IllegalOperationException("只能发布草稿的试卷");
        }
        if(!Objects.equals(exam.getCreator(), teacherPublishExamDTO.getTeacher_id())){
            throw new IllegalOperationException("教师只能发布自己的试卷");
        }
        if (teacherPublishExamDTO.getStartTime().isBefore(LocalDateTime.now().plusDays(1L))){
            throw new IllegalOperationException("教师只能发布24小时以后的考试");
        }
        Exam insetExam = Exam.builder().id(teacherPublishExamDTO.getExam_id())
                .startTime(teacherPublishExamDTO.getStartTime())
                .durationMinutes(teacherPublishExamDTO.getDurationMinutes()).build();
        examMapper.updateExamSelective(insetExam);
        examClassMapper.replaceExamClasses(exam.getId(),teacherPublishExamDTO.getClassIds());
    }

    @Override
    public void submitGrade(TeacherGradeDTO teacherGradeDTO) {
        //TODO Add teacher check

    }
}
