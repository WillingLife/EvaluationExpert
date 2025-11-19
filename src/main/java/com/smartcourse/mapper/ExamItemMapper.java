package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.StudentScoreQuestionVO;
import com.smartcourse.pojo.vo.exam.sql.GradeShortQuestionSqlVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface ExamItemMapper {

    int insertExamItemsByExamSections(@Param("sections") List<ExamSection> sections);

    List<StudentScoreQuestionVO> getQuestion(Long sectionId, Long scoreId);

    GradeShortQuestionSqlVO getStudentAnswer(Long scoreId, Long examItemId);


}
