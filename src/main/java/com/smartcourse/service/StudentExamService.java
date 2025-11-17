package com.smartcourse.service;

import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.vo.exam.StudentExamVO;

public interface StudentExamService {
    StudentExamVO getStudentExamPaper(StudentGetExamDTO studentGetExamDTO);
}
