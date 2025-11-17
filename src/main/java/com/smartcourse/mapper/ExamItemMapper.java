package com.smartcourse.mapper;

import com.smartcourse.pojo.entity.ExamSection;
import com.smartcourse.pojo.vo.exam.StudentScoreQuestionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExamItemMapper {

    int insertExamItemsByExamSections(@Param("sections") List<ExamSection> sections);

    List<StudentScoreQuestionVO> getQuestion(Long sectionId, Long scoreId);
}
