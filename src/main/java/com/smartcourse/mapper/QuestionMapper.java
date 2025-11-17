package com.smartcourse.mapper;

import com.smartcourse.pojo.vo.exam.question.StudentExamChoiceQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamFillBlankQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamShortAnswerQuestionVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {

    List<StudentExamChoiceQuestionVO> getChoice(List<Long> choiceQuestionIds);

    List<StudentExamFillBlankQuestionVO> getFill(List<Long> fillBlankIds);

    List<StudentExamShortAnswerQuestionVO> getShort(List<Long> shortanswerIds);
}
