package com.smartcourse.service.impl;

import com.smartcourse.mapper.ExamMapper;
import com.smartcourse.mapper.ExamSectionMapper;
import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.StudentExamVO;
import com.smartcourse.service.StudentExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentExamServiceImpl implements StudentExamService {
    @Autowired
    ExamMapper examMapper;

    @Autowired
    ExamSectionMapper examSectionMapper;


    /**
     *
     * @param studentGetExamDTO
     * @return
     */
    @Override
    public StudentExamVO getStudentExamPaper(StudentGetExamDTO studentGetExamDTO) {
        Long examId = studentGetExamDTO.getExamId();
        Exam exam = examMapper.getById(examId);
        List<ExamSection> exceptions = examSectionMapper.getById(examId);

        /*
        查询过程为：
        查询1：根据examId在exam表查询Exam实体类
        查询2：根据examId在exam_section表查询List<ExamSection>
        查询3：根据List<ExamSection>得到的section_id列表查询exam_item表，返回Map<Long,List<Long>>,key为section_id,
        value为question_id列表
        查询4：合并成整个question_id列表，向question表查询，根据question表的不同type字段向相应的表进行条件查询，返回数据
        并发顺序为1
        2->3->4
        优化方法为：
        查询2和查询3合并为1次连表查询
        提前分好组别（单/多选，填空，选择），使用CompletableFuture并发3次查询
         */
        return null;
    }
}
