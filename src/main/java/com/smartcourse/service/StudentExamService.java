package com.smartcourse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.dto.exam.StudentExamDTO;
import com.smartcourse.pojo.vo.exam.ExamScoreVO;
import com.smartcourse.pojo.vo.exam.StudentExamListVO;
import com.smartcourse.pojo.vo.exam.StudentExamVO;

public interface StudentExamService {
    StudentExamVO getStudentExamPaper(StudentGetExamDTO studentGetExamDTO);

    void submit(StudentExamDTO studentExamDTO);

    ExamScoreVO getScore(StudentGetExamDTO studentGetExamDTO) throws JsonProcessingException;

    StudentExamListVO getList(Long studentGetExamListDTO);
}
