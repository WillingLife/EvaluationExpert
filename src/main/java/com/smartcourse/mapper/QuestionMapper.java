package com.smartcourse.mapper;

import com.smartcourse.pojo.vo.exam.StudentScoreQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamChoiceQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamFillBlankQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamShortAnswerQuestionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface QuestionMapper {

    List<StudentExamChoiceQuestionVO> getChoice(List<Long> choiceQuestionIds);

    List<StudentExamFillBlankQuestionVO> getFill(List<Long> fillBlankIds);

    List<StudentExamShortAnswerQuestionVO> getShort(List<Long> shortanswerIds);

    @Select("select answer from evaluation_expert.question_short_answer where question_id = #{questionId}")
    String getShortAnswer(Long questionId);

    @Select("select answer from evaluation_expert.question_fill_blank where question_id = #{questionId}")
    List<String> getFillAnswer(Long questionId);

    @Select("select correct from evaluation_expert.question_option where question_id = #{questionId}")
    Set<Integer> getChoiceAnswer(Long questionId);

    @Select("select sort_order as optionId, content from evaluation_expert.question_option where question_id = #{questionId}")
    List<StudentScoreQuestionVO.Option> getOptions(Long questionId);
}
