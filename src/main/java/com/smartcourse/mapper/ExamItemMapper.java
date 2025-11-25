package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.StudentScoreQuestionVO;
import com.smartcourse.pojo.vo.exam.sql.GradeShortQuestionSqlVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ExamItemMapper {

    int insertExamItemsByExamSections(@Param("sections") List<ExamSection> sections);

    List<StudentScoreQuestionVO> getQuestion(Long sectionId, Long scoreId);

    GradeShortQuestionSqlVO getStudentAnswer(Long scoreId, Long examItemId);

    int deleteByExamId(@Param("examId") Long examId);



    @Select("select score from evaluation_expert.exam_item where section_id = #{sectionId}")
    BigDecimal getScore(Long sectionId);

    @Select("select id from evaluation_expert.exam_item where section_id = #{sectionId} and question_id = #{questionId}")
    Long getId(Long sectionId, Long questionId);
}
