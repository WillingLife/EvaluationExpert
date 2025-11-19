package com.smartcourse.service.impl;

import com.smartcourse.converter.ExamConverter;
import com.smartcourse.converter.ExamScoreItemConverter;
import com.smartcourse.enums.ExamStatusEnum;
import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.exception.SqlErrorException;
import com.smartcourse.mapper.*;
import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.pojo.entity.ExamScoreItem;
import com.smartcourse.pojo.vo.exam.TeacherGetExamVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerItemVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;
import com.smartcourse.pojo.vo.exam.items.TeacherGetExamItemVO;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TeacherExamServiceImpl implements TeacherExamService {

    private final ExamConverter examConverter;
    private final ExamMapper examMapper;
    private final ExamSectionMapper examSectionMapper;
    private final ExamItemMapper examItemMapper;
    private final ExamClassMapper examClassMapper;
    private final ExamScoreItemMapper examScoreItemMapper;
    private final ExamScoreItemConverter  examScoreItemConverter;
    private final ExamScoreMapper examScoreMapper;

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
            exam.batchUpdateSectionIdIntoExamItems();
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
                .durationMinutes(teacherPublishExamDTO.getDurationMinutes())
                .status(ExamStatusEnum.PUBLISHED.getValue())
                .build();
        examMapper.updateExamSelective(insetExam);
        if(teacherPublishExamDTO.getClassIds()!=null&& !teacherPublishExamDTO.getClassIds().isEmpty()){
            examClassMapper.deleteExamClassesByExamId(exam.getId());
            examClassMapper.insertExamClasses(exam.getId(), teacherPublishExamDTO.getClassIds());
        }

    }

    @Override
    @Transactional
    public void submitGrade(TeacherGradeDTO teacherGradeDTO) {
        //TODO Add teacher check
        List<ExamScoreItem> items = examScoreItemConverter.teacherGradeItemsToExamScoreItems(
                teacherGradeDTO.getGrades(), teacherGradeDTO.getExamScoreId());
        examScoreItemMapper.batchUpdateExamScoreItemSelectiveByScoreIdAndExamItemId(items);
    }

    @Override
    public TeacherViewAnswerVO viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO) {
        List<TeacherViewAnswerItemVO> studentAnswers = examScoreMapper.getStudentAnswers(teacherViewAnswerDTO.getExamId(),
                teacherViewAnswerDTO.getStudentId());
        return new TeacherViewAnswerVO(studentAnswers);
    }

    @Override
    public TeacherGetExamVO getExamList(TeacherGetExamListDTO teacherGetExamListDTO) {
        List<TeacherGetExamItemVO> list = examMapper.getTeacherGetExamItemVOListByCourseId(teacherGetExamListDTO.getCourseId());
        return new TeacherGetExamVO(list);
    }

    @Override
    public void deleteExam(TeacherDeleteExamDTO teacherDeleteExamDTO) {
        Exam exam = examMapper.getById(teacherDeleteExamDTO.getExamId());
        if (exam == null || Objects.equals(teacherDeleteExamDTO.getTeacherId(), exam.getCreator()) ||
                !Objects.equals(exam.getStatus(), ExamStatusEnum.DRAFT.getValue())){
            throw new  IllegalOperationException("只能删除自己草稿状态的试卷");
        }
        examMapper.deleteExamById(exam.getId());
    }
}
