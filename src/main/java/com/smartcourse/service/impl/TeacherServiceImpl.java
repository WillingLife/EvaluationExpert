package com.smartcourse.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.smartcourse.constant.MessageConstant;
import com.smartcourse.exception.*;
import com.smartcourse.mapper.QuestionFillBlankMapper;
import com.smartcourse.mapper.QuestionMapper;
import com.smartcourse.mapper.QuestionOptionMapper;
import com.smartcourse.mapper.QuestionShortAnswerMapper;
import com.smartcourse.pojo.dto.QuestionAddDTO;
import com.smartcourse.pojo.entity.Question;
import com.smartcourse.pojo.entity.QuestionFillBlank;
import com.smartcourse.pojo.entity.QuestionOption;
import com.smartcourse.pojo.entity.QuestionShortAnswer;
import com.smartcourse.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionFillBlankMapper questionFillBlankMapper;
    private final QuestionShortAnswerMapper questionShortAnswerMapper;

    /**
     * 新增题目
     * @param questionAddDTO 题目信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addQuestion(QuestionAddDTO questionAddDTO) {
        // 基本校验
        if (questionAddDTO.getType() == null) {
            throw new QuestionValidationException(MessageConstant.QUESTION_TYPE_EMPTY);
        }

        // 解析题目信息
        Question question = new Question();
        BeanUtils.copyProperties(questionAddDTO, question);

        // 补全基本信息
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());

        // 插入题目表
        questionMapper.insert(question);

        // 后续按题型处理详细信息
        Integer type = questionAddDTO.getType();
        JsonNode details = questionAddDTO.getDetails();

        if (details == null || details.isEmpty()) {
            throw new QuestionValidationException(MessageConstant.DETAILS_MISSING);
        }

        if (type == 1 || type == 2) {
            // 单选题
            List<QuestionOption> options = JSON.parseArray(details.get("options")
                    .toString(), QuestionOption.class);

            if (options == null || options.isEmpty()) {
                throw new ChoiceQuestionException(MessageConstant.CHOICE_OPTIONS_EMPTY);
            }

            long correctCount = options.stream()
                    .peek(o -> o.setQuestionId(question.getId()))
                    .filter(o -> o.getCorrect() != null && o.getCorrect() == 1)
                    .count();

            if (type == 1 && correctCount != 1) {
                throw new ChoiceQuestionException(MessageConstant.SINGLE_CHOICE_CORRECT_COUNT_INVALID);
            }

            if (type == 2 && correctCount < 1) {
                throw new ChoiceQuestionException(MessageConstant.MULTIPLE_CHOICE_NO_CORRECT);
            }

            // 插入选择题表
            questionOptionMapper.insertBatch(options);
        } else if (type == 3) {
            // 填空题
            List<QuestionFillBlank> blanks = JSON.parseArray(details.get("blanks").toString(),
                    QuestionFillBlank.class);

            if (blanks == null || blanks.isEmpty()) {
                throw new FillBlankQuestionException(MessageConstant.BLANK_ANSWER_EMPTY);
            }

            blanks = blanks.stream()
                    .peek(o -> o.setQuestionId(question.getId()))
                    .toList();

            questionFillBlankMapper.insertBatch(blanks);
        } else if (type == 4) {
            // 简答题
            QuestionShortAnswer answer = JSON.parseObject(details.toString(), QuestionShortAnswer.class);

            if (answer == null) {
                throw new DataParseException(MessageConstant.DATA_PARSE_ERROR);
            }

            if (answer.getAnswer() == null || answer.getAnswer().isEmpty()) {
                throw new ShortAnswerQuestionException(MessageConstant.SHORT_ANSWER_ANSWER_EMPTY);
            }
            answer.setQuestionId(question.getId());

            questionShortAnswerMapper.insert(answer);
        } else {
            throw new QuestionValidationException(MessageConstant.QUESTION_TYPE_INVALID);
        }
    }
}
