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
import com.smartcourse.pojo.dto.QuestionQueryDTO;
import com.smartcourse.pojo.dto.QuestionUpdateDTO;
import com.smartcourse.pojo.entity.Question;
import com.smartcourse.pojo.entity.QuestionFillBlank;
import com.smartcourse.pojo.entity.QuestionOption;
import com.smartcourse.pojo.entity.QuestionShortAnswer;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.result.PageResult;
import com.smartcourse.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private static final String BLANKS = "blanks";
    private static final String OPTIONS = "options";

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionFillBlankMapper questionFillBlankMapper;
    private final QuestionShortAnswerMapper questionShortAnswerMapper;
    private final ObjectMapper objectMapper;

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
            // 选择 题
            List<QuestionOption> options = JSON.parseArray(details.get(OPTIONS)
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
            List<QuestionFillBlank> blanks = JSON.parseArray(details.get(BLANKS).toString(),
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

    /**
     * 分页查询题目数据
     * @param questionQueryDTO 查询条件
     * @return 题目数据
     */
    @Override
    public PageResult<QuestionQueryVO> page(QuestionQueryDTO questionQueryDTO) {
        Integer page = questionQueryDTO.getPage();
        Integer pageSize = questionQueryDTO.getPageSize();

        // 手动分页
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        // 计算起始页
        int offset = (page - 1) * pageSize;

        // 统计数据量
        Long total = questionMapper.count(
                questionQueryDTO.getCourseId(),
                questionQueryDTO.getStem(),
                questionQueryDTO.getDifficulty()
        );

        // 当前不存在数据
        if (total == null || total == 0) {
            return new PageResult<>(0, Collections.emptyList());
        }

        // 分页查询
        List<QuestionQueryVO> records = questionMapper.pageList(
                questionQueryDTO.getCourseId(),
                questionQueryDTO.getStem(),
                questionQueryDTO.getDifficulty(),
                offset,
                pageSize
        );

        // 各种题型对应题目id
        List<Long> optionIds = new ArrayList<>();
        List<Long> blankIds = new ArrayList<>();
        List<Long> shortIds = new ArrayList<>();

        for (QuestionQueryVO vo : records) {
            if (vo.getType() == null) continue;
            if (vo.getType() == 1 || vo.getType() == 2) optionIds.add(vo.getId());
            else if (vo.getType() == 3) blankIds.add(vo.getId());
            else if (vo.getType() == 4) shortIds.add(vo.getId());
        }

        // 每个题目对应的题目详情信息
        Map<Long, List<QuestionOption>> optionMap = new HashMap<>();
        Map<Long, List<QuestionFillBlank>> blankMap = new HashMap<>();
        Map<Long, QuestionShortAnswer> shortMap = new HashMap<>();

        // 查出所有选择题
        if (!optionIds.isEmpty()) {
            List<QuestionOption> options = questionOptionMapper.selectByQuestionIds(optionIds);
            for (QuestionOption o : options) {
                optionMap.computeIfAbsent(o.getQuestionId(),
                        k -> new ArrayList<>()).add(o);
            }
        }

        // 查出所有填空题
        if (!blankIds.isEmpty()) {
            List<QuestionFillBlank> blanks = questionFillBlankMapper.selectByQuestionIds(blankIds);
            for (QuestionFillBlank b : blanks) {
                blankMap.computeIfAbsent(b.getQuestionId(),
                        k -> new ArrayList<>()).add(b);
            }
        }

        // 查出所有简答题
        if (!shortIds.isEmpty()) {
            List<QuestionShortAnswer> answers = questionShortAnswerMapper.selectByQuestionIds(shortIds);
            for (QuestionShortAnswer sa : answers) {
                shortMap.put(sa.getQuestionId(), sa);
            }
        }

        for (QuestionQueryVO vo : records) {
            Integer type = vo.getType();
            Long questionId = vo.getId();

            if (type == null) continue;
            if (type == 1 || type == 2) {
                List<QuestionOption> options =
                        optionMap.getOrDefault(questionId, Collections.emptyList());

                ObjectNode node = objectMapper.createObjectNode();
                node.set(OPTIONS, objectMapper.valueToTree(options));

                vo.setDetails(node);
            } else if (type == 3) {
                List<QuestionFillBlank> blanks =
                        blankMap.getOrDefault(questionId, Collections.emptyList());

                ObjectNode node = objectMapper.createObjectNode();
                node.set(BLANKS, objectMapper.valueToTree(blanks));

                vo.setDetails(node);
            } else if (type == 4) {
                QuestionShortAnswer sa = shortMap.get(questionId);
                vo.setDetails(objectMapper.valueToTree(sa));
            }
        }

        return new PageResult<>(total, records);
    }

    @Override
    public QuestionQueryVO get(Long id) {
        Question question = questionMapper.selectById(id);

        if (question == null) {
            return null;
        }

        QuestionQueryVO questionQueryVO = new QuestionQueryVO();
        BeanUtils.copyProperties(question, questionQueryVO);

        // 设置题目详细信息
        getQuestionDetails(questionQueryVO);

        return questionQueryVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QuestionUpdateDTO questionUpdateDTO) {
        Long questionId = questionUpdateDTO.getId();

        if (questionId == null) {
            throw new QuestionValidationException(MessageConstant.QUESTION_NOT_EXIST);
        }

        // 查询数据库
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new DataParseException(MessageConstant.DATA_PARSE_ERROR);
        }

        // 更新题目信息
        BeanUtils.copyProperties(questionUpdateDTO,question);
        question.setUpdateTime(LocalDateTime.now());

        // 更新数据库
        questionMapper.update(question);

        // 更新题目对应详情
        Integer type = questionUpdateDTO.getType() != null ?
                questionUpdateDTO.getType() : question.getType();

        JsonNode details = questionUpdateDTO.getDetails();

        // 先减后增
        if (type == 1 || type == 2) { // 选择
            questionOptionMapper.deleteByQuestionId(questionId);

            // 检验是否要更新具体信息
            if (details == null || details.isEmpty()) {
                return;
            }

            List<QuestionOption> options = JSON.parseArray(details.get(OPTIONS).toString(), QuestionOption.class);

            if (options != null && !options.isEmpty()) {
                options = options.stream()
                        .peek(o -> o.setQuestionId(questionId))
                        .toList();

                // 插入选择题选项信息
                questionOptionMapper.insertBatch(options);
            } else {
                throw new ChoiceQuestionException(MessageConstant.CHOICE_OPTIONS_EMPTY);
            }
        } else if (type == 3) { // 填空
            questionFillBlankMapper.deleteByQuestionId(questionId);

            // 检验是否要更新具体信息
            if (details == null || details.isEmpty()) {
                return;
            }

            List<QuestionFillBlank> blanks = JSON.parseArray(details.get(BLANKS).toString(), QuestionFillBlank.class);

            if (blanks != null && !blanks.isEmpty()) {
                blanks = blanks.stream()
                        .peek(b -> b.setQuestionId(questionId))
                        .toList();

                // 插入填空题信息
                questionFillBlankMapper.insertBatch(blanks);
            } else {
                throw new FillBlankQuestionException(MessageConstant.BLANK_ANSWER_EMPTY);
            }
        } else if (type == 4) { // 简答
            questionShortAnswerMapper.deleteByQuestionId(questionId);

            // 检验是否要更新具体信息
            if (details == null || details.isEmpty()) {
                return;
            }

            QuestionShortAnswer shortAnswer = JSON.parseObject(details.toString(), QuestionShortAnswer.class);
            if (shortAnswer != null) {
                shortAnswer.setQuestionId(questionId);

                // 插入简答题信息
                questionShortAnswerMapper.insert(shortAnswer);
            } else  {
                throw new DataParseException(MessageConstant.DATA_PARSE_ERROR);
            }
        } else {
            throw new QuestionValidationException(MessageConstant.QUESTION_TYPE_INVALID);
        }
    }

    private void getQuestionDetails(QuestionQueryVO questionQueryVO) {
        Integer type = questionQueryVO.getType();
        Long questionId = questionQueryVO.getId();

        if (type == 1 || type == 2) { // 选择
            List<QuestionOption> options =
                    questionOptionMapper.selectByQuestionId(questionId);

            ObjectNode node = objectMapper.createObjectNode();
            node.set(OPTIONS, objectMapper.valueToTree(options));

            questionQueryVO.setDetails(node);
        } else if (type == 3) { // 填空
            List<QuestionFillBlank> blanks =
                    questionFillBlankMapper.selectByQuestionId(questionId);

            ObjectNode node = objectMapper.createObjectNode();
            node.set(BLANKS, objectMapper.valueToTree(blanks));

            questionQueryVO.setDetails(node);
        } else if (type == 4) { // 简答
            QuestionShortAnswer shortAnswer = questionShortAnswerMapper.selectByQuestionId(questionId);
            questionQueryVO.setDetails(objectMapper.valueToTree(shortAnswer));
        }
    }
}
