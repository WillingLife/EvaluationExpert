package com.smartcourse.mapper;

import com.smartcourse.pojo.dto.FillBlankAnswerDTO;
import com.smartcourse.pojo.entity.Question;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.pojo.vo.exam.StudentScoreQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamChoiceQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamFillBlankQuestionVO;
import com.smartcourse.pojo.vo.exam.question.StudentExamShortAnswerQuestionVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface QuestionMapper {
    /**
     * 新增题目信息
     * @param question 题目信息
     */
    void insert(Question question);

    /**
     * 分页查询题目列表
     */
    List<QuestionQueryVO> pageList(Long courseId, String stem, Integer difficulty,
                                   Integer type, Integer offset, Integer pageSize);
    List<StudentExamChoiceQuestionVO> getChoice(List<Long> choiceQuestionIds);

    List<StudentExamFillBlankQuestionVO> getFill(List<Long> fillBlankIds);

    List<StudentExamShortAnswerQuestionVO> getShort(List<Long> shortanswerIds);

    @Select("select answer from evaluation_expert.question_short_answer where question_id = #{questionId}")
    String getShortAnswer(Long questionId);

    @Select("select answer from evaluation_expert.question_fill_blank where question_id = #{questionId}")
    List<String> getFillAnswer(Long questionId);

    /**
     * 统计题目数量
     */
    Long count(Long courseId, String stem, Integer difficulty, Integer type);
    @Select("select correct from evaluation_expert.question_option where question_id = #{questionId}")
    Set<Integer> getChoiceAnswer(Long questionId);

    /**
     * 根据ID查询题目
     */
    Question selectById(Long id);

    /**
     * 根据ID批量查询题目
     */
    List<Question> selectByIds(@Param("ids") List<Long> ids);
    @Select("select sort_order as optionId, content from evaluation_expert.question_option where question_id = #{questionId}")
    List<StudentScoreQuestionVO.Option> getOptions(Long questionId);

    /**
     * 更新题目信息
     * @param question 新题目信息
     */
    void update(Question question);

    @Select("select blank_index as blankIndex, answer from evaluation_expert.question_fill_blank where question_id = #{questionId}")
    List<FillBlankAnswerDTO> getFAnswer(Long questionId);

    @Select("select id from evaluation_expert.question_option where question_id = #{questionId} and correct = 1")
    Long getCAnswer(Long questionId);

    @Select("select id from evaluation_expert.question_option where question_id = #{questionId} and correct = 1")
    List<Long> getCAnswers(Long questionId);
}
