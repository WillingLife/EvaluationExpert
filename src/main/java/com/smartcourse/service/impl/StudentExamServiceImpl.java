package com.smartcourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smartcourse.enums.QuestionTypeEnum;
import com.smartcourse.mapper.*;
import com.smartcourse.pojo.dto.FillBlankAnswerDTO;
import com.smartcourse.pojo.dto.StudentGetExamDTO;
import com.smartcourse.pojo.dto.exam.*;
import com.smartcourse.pojo.entity.*;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.pojo.vo.exam.*;
import com.smartcourse.pojo.vo.exam.question.*;
import com.smartcourse.service.AsyncQuestionService;
import com.smartcourse.service.StudentExamService;
import com.smartcourse.utils.MultipleChoiceScoreCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentExamServiceImpl implements StudentExamService {
    @Autowired
    ExamMapper examMapper;

    @Autowired
    ExamScoreMapper examScoreMapper;

    @Autowired
    ExamSectionMapper examSectionMapper;

    @Autowired
    ExamScoreItemMapper examScoreItemMapper;

    @Autowired
    ExamItemMapper examItemMapper;

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AsyncQuestionService asyncQuestionService;


    /**
     * @param studentGetExamDTO
     * @return
     */
    @Override
    @Transactional
    public StudentExamVO getStudentExamPaper(StudentGetExamDTO studentGetExamDTO) {
        Long examId = studentGetExamDTO.getExamId();
        Exam exam = examMapper.getById(examId);
        List<SectionItemDTO> list = examSectionMapper.getSectionItems(examId);

        System.out.println(list);

        // 按 sectionId 分组
        Map<Long, List<SectionItemDTO>> groupMap = list.stream()
                .collect(Collectors.groupingBy(SectionItemDTO::getId));

        // 转换为 ExamSectionWithIdsVO
        List<ExamSectionWithIdsVO> collect = groupMap.values().stream()
                .map(items -> {
                    SectionItemDTO first = items.get(0);

                    ExamSectionWithIdsVO vo = new ExamSectionWithIdsVO();
                    // 复制 Section 字段
                    BeanUtils.copyProperties(first, vo);

                    // 提取 questionId 列表（过滤 null）
                    List<Long> questionIds = items.stream()
                            .map(SectionItemDTO::getQuestionId)
                            .filter(id -> id != null)
                            .collect(Collectors.toList());

                    vo.setQuestionIds(questionIds);
                    return vo;
                })
                .sorted((a, b) -> a.getOrderNo().compareTo(b.getOrderNo())) // 按 orderNo 排序
                .toList();
        List<Long> choiceQuestionIds = new ArrayList<>();
        List<Long> fillBlankIds = new ArrayList<>();
        List<Long> shortanswerIds = new ArrayList<>();

        for (ExamSectionWithIdsVO vo : collect) {
            if (vo.getQuestionType().equals("fill_blank")) {
                fillBlankIds.addAll(vo.getQuestionIds());
            } else if (vo.getQuestionType().equals("short_answer")) {
                shortanswerIds.addAll(vo.getQuestionIds());
            } else {
                choiceQuestionIds.addAll(vo.getQuestionIds());
            }
        }

        // 并发执行3次查询
        CompletableFuture<List<StudentExamChoiceQuestionVO>> choiceFuture =
                asyncQuestionService.getChoiceAsync(choiceQuestionIds);

        CompletableFuture<List<StudentExamFillBlankQuestionVO>> fillBlankFuture =
                asyncQuestionService.getFillAsync(fillBlankIds);

        CompletableFuture<List<StudentExamShortAnswerQuestionVO>> shortAnswerFuture =
                asyncQuestionService.getShortAsync(shortanswerIds);

        // 等待所有任务完成并获取结果
        CompletableFuture.allOf(choiceFuture, fillBlankFuture, shortAnswerFuture).join();

        List<StudentExamChoiceQuestionVO> studentExamChoiceQuestionVOS = choiceFuture.join();
        List<StudentExamFillBlankQuestionVO> studentExamFillBlankQuestionVOS = fillBlankFuture.join();
        List<StudentExamShortAnswerQuestionVO> studentExamShortAnswerQuestionVOS = shortAnswerFuture.join();

        System.out.println("1 " + studentExamChoiceQuestionVOS);
        System.out.println("2 " + studentExamFillBlankQuestionVOS);
        System.out.println("3 " + studentExamShortAnswerQuestionVOS);

        List<StudentExamSectionVO> studentExamSectionVOS = new ArrayList<>();
        for (ExamSectionWithIdsVO vo : collect) {
            StudentExamSectionVO studentExamSectionVO = new StudentExamSectionVO();
            studentExamSectionVO.setSectionId(vo.getId());
            studentExamSectionVO.setTitle(vo.getTitle());
            studentExamSectionVO.setQuestionType(QuestionTypeEnum.fromValue(vo.getQuestionType()));
            studentExamSectionVO.setQuestionNumber(vo.getQuestionIds().size());
            studentExamSectionVO.setDescription(vo.getDescription());
            studentExamSectionVO.setChoiceNegativeScore(vo.getChoiceNegativeScore());
            studentExamSectionVO.setMultipleStrategy(vo.getMultipleStrategy());
            studentExamSectionVO.setMultipleStrategyConf(vo.getMultipleStrategyConf());
            List<Long> questionIds = vo.getQuestionIds();
            List<StudentExamQuestionVO> questions = new ArrayList<>();
            if (vo.getQuestionType().equals("fill_blank")) {
                for (StudentExamFillBlankQuestionVO studentExamFillBlankQuestionVO : studentExamFillBlankQuestionVOS) {
                    if (questionIds.contains(studentExamFillBlankQuestionVO.getQuestionId())) {
                        questions.add(studentExamFillBlankQuestionVO);
                    }
                }
            } else if (vo.getQuestionType().equals("short_answer")) {
                for (StudentExamShortAnswerQuestionVO studentExamShortAnswerQuestionVO : studentExamShortAnswerQuestionVOS) {
                    if (questionIds.contains(studentExamShortAnswerQuestionVO.getQuestionId())) {
                        questions.add(studentExamShortAnswerQuestionVO);
                    }
                }
            } else {
                for (StudentExamChoiceQuestionVO studentExamChoiceQuestionVO : studentExamChoiceQuestionVOS) {
                    if (questionIds.contains(studentExamChoiceQuestionVO.getQuestionId())) {
                        questions.add(studentExamChoiceQuestionVO);
                        studentExamSectionVO.setChoiceScore(studentExamChoiceQuestionVO.getScore());
                    }
                }
            }
            studentExamSectionVO.setQuestions(questions);
            studentExamSectionVOS.add(studentExamSectionVO);
        }

        StudentExamVO studentExamVO = new StudentExamVO();
        studentExamVO.setExamId(examId);
        studentExamVO.setExamName(exam.getName());
        studentExamVO.setExamNotice(exam.getNotice());
        studentExamVO.setStartTime(exam.getStartTime());
        studentExamVO.setDurationMinutes(exam.getDurationMinutes());
        studentExamVO.setCourseId(exam.getCourseId());
        studentExamVO.setDescription(exam.getDescription());
        studentExamVO.setTotalScore(exam.getTotalScore());
        studentExamVO.setPassScore(exam.getPassScore());
        studentExamVO.setSections(studentExamSectionVOS);

        return studentExamVO;
        /*
        查询过程为：
        查询1：根据examId在exam表查询Exam实体类
        查询2：根据examId在exam_section表查询List<ExamSection>
        查询3：根据List<ExamSection>得到的section_id列表查询exam_item表，返回Map<Long,List<Long>>,key为section_id,
        value为question_id列表
        查询4：合并成整个question_id列表，向question表查询，根据question表的不同type字段向相应的表进行条件查询，返回数据
        并发顺序为1
        2->3->4
        优化方法为：
        查询2和查询3合并为1次连表查询
        提前分好组别（单/多选，填空，选择），使用CompletableFuture并发3次查询
         */
    }

    @Override
    @Transactional
    public void submit(StudentExamDTO studentExamDTO) {
        ExamScore examScore = new ExamScore();
        examScore.setExamId(studentExamDTO.getExamId());
        examScore.setStudentId(studentExamDTO.getStudentId());
        examScore.setStatus("submitted");
        examScore.setStartTime(studentExamDTO.getStartTime());
        examScore.setSubmitTime(studentExamDTO.getSubmitTime());
        examScore.setCreateTime(LocalDateTime.now());
        examScore.setUpdateTime(LocalDateTime.now());
        if (studentExamDTO.getStartTime() != null && studentExamDTO.getSubmitTime() != null) {
            Duration duration = Duration.between(studentExamDTO.getStartTime(), studentExamDTO.getSubmitTime());
            examScore.setDurationSeconds(Math.toIntExact(duration.getSeconds()));
        } else {
            throw new IllegalArgumentException("提交时间和开始时间不能为0");
        }

        examScoreMapper.submit(examScore);
        Long scoreId = examScore.getId();
        BigDecimal totalScore = BigDecimal.valueOf(0);

        List<ExamScoreItemDTO> examScoreItems = new ArrayList<>();
        for (StudentExamSectionDTO section : studentExamDTO.getSections()) {
            String questionType = section.getQuestionType().getValue();
            Long sectionId = section.getSectionId();
            BigDecimal questionScore = examItemMapper.getScore(sectionId);
            for (StudentExamQuestionDTO question : section.getQuestions()) {
                ExamScoreItemDTO examScoreItem = new ExamScoreItemDTO();
                Long questionId = question.getQuestionId();
                Long examItemId = examItemMapper.getId(sectionId, questionId);
                String answerJson = null;
                BigDecimal score = BigDecimal.valueOf(0);
                try {
                    answerJson = switch (questionType) {
                        case "single" -> {
                            // 处理选择题
                            List<Long> choices = question.getChoiceAnswer();
                            Long optionId = questionMapper.getCAnswer(questionId);
                            if (!choices.isEmpty()) {
                                Long choice = choices.get(0);
                                if (Objects.equals(optionId, choice)) {
                                    score = questionScore;
                                }
                            }
                            yield objectMapper.writeValueAsString(choices);
                        }
                        case "multiple" -> {
                            // 处理多选题
                            List<Long> choices = question.getChoiceAnswer();
                            String multipleStrategy = examSectionMapper.get(sectionId);
                            String multipleStrategyConf = examSectionMapper.getConf(sectionId);
                            // 解析策略配置
                            Map<String, Object> strategyConfig = parseStrategyConfig(multipleStrategyConf);

                            // 获取正确答案和用户答案
                            List<Long> correctAnswers = questionMapper.getCAnswers(questionId);
                            List<Long> userAnswers = choices != null ? choices : Collections.emptyList();

                            // 计算各项统计指标
                            MultipleChoiceScoreCalculator.MultipleChoiceStrategyContext context =
                                    calculateChoiceStatistics(correctAnswers, userAnswers, questionScore, strategyConfig);

                            // 计算得分
                            score = MultipleChoiceScoreCalculator.calculate(multipleStrategy, context);
                            yield objectMapper.writeValueAsString(choices);
                        }
                        case "fill_blank" -> {
                            // 处理填空题
                            List<String> blanks = question.getFillBlankAnswer();
                            int length = blanks.size();
                            if (length == 0) {
                                yield objectMapper.writeValueAsString(blanks);
                            }
                            int i = 1;
                            BigDecimal scoreBank = questionScore.divide(new BigDecimal(length), 2, RoundingMode.HALF_UP);
                            List<FillBlankAnswerDTO> answers = questionMapper.getFAnswer(questionId);
                            Map<Integer, List<String>> fAnswer = answers.stream()
                                    .collect(Collectors.groupingBy(
                                            FillBlankAnswerDTO::getBlankIndex,
                                            Collectors.mapping(FillBlankAnswerDTO::getAnswer, Collectors.toList())
                                    ));
                            for (String blank : blanks) {
                                List<String> answer = fAnswer.get(i);
                                if (answer.contains(blank)) {
                                    score = score.add(scoreBank);
                                }
                                i++;
                            }
                            yield objectMapper.writeValueAsString(blanks);
                        }
                        case "short_answer" -> {
                            // TODO 简答题AI通用 function(scoreId,examItemId) 学生答案可能为空
                            // 处理简答题
                            String answer = question.getShortAnswer();
                            yield objectMapper.writeValueAsString(answer);
                        }
                        default -> answerJson;
                    };
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                examScoreItem.setScoreId(scoreId);
                examScoreItem.setAnswer(answerJson);
                examScoreItem.setCreateTime(LocalDateTime.now());
                examScoreItem.setUpdateTime(LocalDateTime.now());
                examScoreItem.setQuestionId(question.getQuestionId());
                examScoreItem.setScore(score);
                totalScore = totalScore.add(score);
                examScoreItems.add(examScoreItem);
            }
        }

        examScoreMapper.update(totalScore, scoreId);
        examScoreItemMapper.submit(examScoreItems);
    }

    @Override
    public ExamScoreVO getScore(StudentGetExamDTO studentGetExamDTO) throws JsonProcessingException {
        Long examId = studentGetExamDTO.getExamId();

        ExamScoreVO examScoreVO = examScoreMapper.getScore(studentGetExamDTO);

        Exam exam = examMapper.getById(examId);
        examScoreVO.setExamName(exam.getName());
        examScoreVO.setStartTime(exam.getStartTime());

        List<StudentScoreSectionVO> studentScoreSectionVOS = examSectionMapper.getById(examId);
        for (StudentScoreSectionVO studentScoreSectionVO : studentScoreSectionVOS) {
            Long sectionId = studentScoreSectionVO.getSectionId();
            String type = String.valueOf(studentScoreSectionVO.getQuestionType());
            List<StudentScoreQuestionVO> questionVOS = examItemMapper.getQuestion(sectionId, examScoreVO.getId());
            for (StudentScoreQuestionVO questionVO : questionVOS) {
                if (type.equals("short_answer")) {
                    String answerJson = questionVO.getAnswer();
                    String str = objectMapper.readValue(answerJson, new TypeReference<String>() {
                    });
                    questionVO.setStudentAnswer(str);
                    questionVO.setCorrectAnswer(questionMapper.getShortAnswer(questionVO.getQuestionId()));
                } else if (type.equals("fill_blank")) {
                    String answerJson = questionVO.getAnswer();
                    List<String> list = objectMapper.readValue(answerJson, new TypeReference<List<String>>() {
                    });
                    questionVO.setStudentAnswer(list);
                    List<String> answerList = questionMapper.getFillAnswer(questionVO.getQuestionId());
                    questionVO.setBlankCount(answerList.size());
                    questionVO.setCorrectAnswer(answerList);
                } else {
                    String answerJson = questionVO.getAnswer();
                    List<Integer> list = objectMapper.readValue(answerJson, new TypeReference<List<Integer>>() {
                    });
                    questionVO.setStudentAnswer(list);
                    Set<Integer> answerList = questionMapper.getChoiceAnswer(questionVO.getQuestionId());
                    questionVO.setCorrectAnswer(answerList);
                    List<StudentScoreQuestionVO.Option> optionVOS = questionMapper.getOptions(questionVO.getQuestionId());
                    questionVO.setOptions(optionVOS);
                }
            }
            studentScoreSectionVO.setQuestionNumber(questionVOS.size());
            studentScoreSectionVO.setQuestions(questionVOS);
        }
        return examScoreVO;
    }

    @Override
    public StudentExamListVO getList(Long courseId) {
        List<ExamList> list = examMapper.getList(courseId);
        StudentExamListVO studentExamListVO = new StudentExamListVO();
        studentExamListVO.setCourseId(courseId);
        studentExamListVO.setList(list);
        return studentExamListVO;
    }

    private Map<String, Object> parseStrategyConfig(String strategyConf) {
        if (strategyConf == null || strategyConf.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(strategyConf, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to parse strategy config: {}", strategyConf, e);
            return Collections.emptyMap();
        }
    }

    private MultipleChoiceScoreCalculator.MultipleChoiceStrategyContext calculateChoiceStatistics(
            List<Long> correctAnswers, List<Long> userAnswers, BigDecimal fullScore,
            Map<String, Object> strategyConfig) {

        // 确保列表不为null
        correctAnswers = correctAnswers != null ? correctAnswers : Collections.emptyList();
        userAnswers = userAnswers != null ? userAnswers : Collections.emptyList();

        // 计算各项统计
        int correctlySelected = 0;  // 正确选中的选项
        int missedCorrect = 0;      // 漏选的正确选项
        int incorrectlySelected = 0; // 错选的错误选项

        // 统计正确选中的和漏选的
        for (Long correctAnswer : correctAnswers) {
            if (userAnswers.contains(correctAnswer)) {
                correctlySelected++;
            } else {
                missedCorrect++;
            }
        }

        // 统计错选的
        for (Long userAnswer : userAnswers) {
            if (!correctAnswers.contains(userAnswer)) {
                incorrectlySelected++;
            }
        }

        // 总选项数（可能需要从数据库获取）
        int totalOptions = getTotalOptionCount(correctAnswers, userAnswers);

        return MultipleChoiceScoreCalculator.MultipleChoiceStrategyContext.builder()
                .fullScore(fullScore)
                .totalOptionCount(totalOptions)
                .correctlySelectedCount(correctlySelected)
                .missedCorrectCount(missedCorrect)
                .incorrectlySelectedCount(incorrectlySelected)
                .strategyConfig(strategyConfig)
                .build();
    }

    private int getTotalOptionCount(List<Long> correctAnswers, List<Long> userAnswers) {
        // 方法1: 从数据库查询该题目的总选项数
        // return questionMapper.getOptionCount(questionId);

        // 方法2: 根据正确答案和用户答案推导（可能有误差）
        Set<Long> allOptions = new HashSet<>();
        allOptions.addAll(correctAnswers);
        allOptions.addAll(userAnswers);
        return allOptions.size();
    }
}
