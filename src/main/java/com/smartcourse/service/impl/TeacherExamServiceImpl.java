package com.smartcourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.converter.ExamConverter;
import com.smartcourse.converter.ExamScoreItemConverter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smartcourse.converter.KnowledgeNodeConverter;
import com.smartcourse.converter.QuestionConverter;
import com.smartcourse.enums.ExamStatusEnum;
import com.smartcourse.enums.QuestionTypeEnum;
import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.exception.SqlErrorException;
import com.smartcourse.infra.ai.ExamAiAgent;
import com.smartcourse.infra.ai.ExamGenerationContext;
import com.smartcourse.infra.ai.vo.ExamGenCriteria;
import com.smartcourse.infra.redis.ExamSessionRedisRepository;
import com.smartcourse.infra.redis.dto.ExamSessionDTO;
import com.smartcourse.infra.redis.dto.SelectedQuestionItemDTO;
import com.smartcourse.mapper.*;
import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.dto.dify.AiInputKnowledgeNodeDTO;
import com.smartcourse.pojo.dto.exam.TeacherExamAiGenerateDTO;
import com.smartcourse.pojo.dto.exam.stream.AiStreamPayload;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.pojo.entity.ExamScoreItem;
import com.smartcourse.pojo.entity.KnowledgeNode;
import com.smartcourse.pojo.vo.QuestionQueryVO;
import com.smartcourse.pojo.vo.exam.TeacherGetExamVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerItemVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;
import com.smartcourse.pojo.entity.*;
import com.smartcourse.pojo.vo.exam.*;
import com.smartcourse.pojo.vo.exam.items.TeacherGetExamItemVO;
import com.smartcourse.service.QuestionService;
import com.smartcourse.pojo.vo.exam.question.*;
import com.smartcourse.service.AsyncQuestionService;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherExamServiceImpl implements TeacherExamService {

    private final ExamConverter examConverter;
    private final ExamMapper examMapper;
    private final ExamSectionMapper examSectionMapper;
    private final ExamItemMapper examItemMapper;
    private final ExamScoreItemMapper examScoreItemMapper;
    private final ExamScoreItemConverter examScoreItemConverter;
    private final ExamScoreMapper examScoreMapper;
    private final QuestionService questionService;
    private final KnowledgeNodeMapper knowledgeNodeMapper;
    private final KnowledgeNodeConverter knowledgeNodeConverter;
    private final ObjectMapper objectMapper;
    private final ExamAiAgent examAiAgent;
    private final ExamSessionRedisRepository examSessionRedisRepository;
    private final QuestionConverter questionConverter;
    private final AsyncQuestionService asyncQuestionService;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionFillBlankMapper questionFillBlankMapper;
    private final QuestionShortAnswerMapper questionShortAnswerMapper;


    private static final String GENERATING_EVENT = "generating";
    private static final String FINISH_EVENT = "finish";
    private static final String ERROR_EVENT = "error";
    private static final String BLANKS = "blanks";
    private static final String OPTIONS = "options";


    @Override
    @Transactional
    public Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO) {
        Exam exam = examConverter.teacherSaveExamDTOToExam(teacherSaveExamDTO);
        if (exam.getId() == null) {
            createExamWithSections(exam);
        } else {
            refreshExamWithSections(exam);
        }
        return exam.getId();
    }

    private void createExamWithSections(Exam exam) {
        if (examMapper.insertExam(exam) < 1) {
            throw new SqlErrorException("数据库操作失败");
        }
        persistSectionsAndItems(exam);
    }

    private void refreshExamWithSections(Exam exam) {
        if (examMapper.updateExamRecursive(exam) < 1) {
            throw new SqlErrorException("数据库操作失败");
        }
        examItemMapper.deleteByExamId(exam.getId());
        examSectionMapper.deleteByExamId(exam.getId());
        persistSectionsAndItems(exam);
    }

    private void persistSectionsAndItems(Exam exam) {
        if (exam.getSections() == null || exam.getSections().isEmpty()) {
            return;
        }
        exam.batchUpdateExamIdIntoSections();
        examSectionMapper.insetSections(exam.getSections());
        exam.batchUpdateSectionIdIntoExamItems();
        examItemMapper.insertExamItemsByExamSections(exam.getSections());
    }

    @Override
    @Transactional
    public void publishExam(TeacherPublishExamDTO teacherPublishExamDTO) {
        Exam exam = examMapper.getById(teacherPublishExamDTO.getExam_id());
        if (exam == null || !Objects.equals(exam.getStatus(), ExamStatusEnum.DRAFT.getValue())) {
            throw new IllegalOperationException("只能发布草稿的试卷");
        }
        if (!Objects.equals(exam.getCreator(), teacherPublishExamDTO.getTeacher_id())) {
            throw new IllegalOperationException("教师只能发布自己的试卷");
        }
        if (teacherPublishExamDTO.getStartTime().isBefore(LocalDateTime.now().plusDays(1L))) {
            throw new IllegalOperationException("教师只能发布24小时以后的考试");
        }
        Exam insetExam = Exam.builder().id(teacherPublishExamDTO.getExam_id())
                .startTime(teacherPublishExamDTO.getStartTime())
                .durationMinutes(teacherPublishExamDTO.getDurationMinutes())
                .status(ExamStatusEnum.PUBLISHED.getValue())
                .build();
        examMapper.updateExamSelective(insetExam);
    }

    @Override
    @Transactional
    public void submitGrade(TeacherGradeAssign teacherGradeDTO) {
        //TODO Add teacher check
        List<ExamScoreItem> items = examScoreItemConverter.teacherGradeItemsToExamScoreItems(
                teacherGradeDTO.getGrades(), teacherGradeDTO.getExamScoreId());
        BigDecimal totalScore = examScoreMapper.getTotalScore(teacherGradeDTO.getExamScoreId());
        for (ExamScoreItem item : items) {
            totalScore = totalScore.add(item.getScore());
        }
        examScoreMapper.finalUpdate(totalScore, teacherGradeDTO.getExamScoreId());
        examScoreItemMapper.batchUpdateExamScoreItemSelectiveByScoreIdAndExamItemId(items);
    }

    @Override
    public TeacherViewAnswerVO viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO) {
        // FIXME get ai score(待测试)
        List<TeacherViewAnswerItemVO> studentAnswers = examScoreMapper.getStudentAnswers(teacherViewAnswerDTO.getExamId(),
                teacherViewAnswerDTO.getStudentId());
        ExamScore examScore = examScoreMapper.getExamScoreByExamIdAndStudentId(teacherViewAnswerDTO.getExamId(),
                teacherViewAnswerDTO.getStudentId());
        return new TeacherViewAnswerVO(examScore.getId(),studentAnswers);
    }

    @Override
    public TeacherGetExamVO getExamList(TeacherGetExamListDTO teacherGetExamListDTO) {
        List<TeacherGetExamItemVO> list = examMapper.getTeacherGetExamItemVOListByCourseId(teacherGetExamListDTO.getCourseId());
        return new TeacherGetExamVO(list);
    }

    @Override
    public void deleteExam(TeacherDeleteExamDTO teacherDeleteExamDTO) {
        Exam exam = examMapper.getById(teacherDeleteExamDTO.getExamId());
        if (exam == null || Objects.equals(teacherDeleteExamDTO.getTeacherId(), exam.getCreator()) ||
                !Objects.equals(exam.getStatus(), ExamStatusEnum.DRAFT.getValue())) {
            throw new IllegalOperationException("只能删除自己草稿状态的试卷");
        }
        examMapper.deleteExamById(exam.getId());
    }

    private String convertListToJson(List<AiInputKnowledgeNodeDTO> knowledgeDTOS) {
        try {
            return objectMapper.writeValueAsString(knowledgeDTOS);
        } catch (JsonProcessingException e) {
            // 处理转换异常
            log.error(e.getMessage());
            throw new RuntimeException("序列化失败", e);
        }
    }

    private String convertIdListToJson(List<SelectedQuestionItemDTO> ids) {
        try {
            return objectMapper.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("序列化失败", e);
        }
    }


    public Flux<AiStreamPayload> aiGenerateExam(TeacherExamAiGenerateDTO dto) {
        // 从redis获取先前数据
        ExamSessionDTO examSessionDTO = examSessionRedisRepository.get(dto.getSessionId());
        List<KnowledgeNode> knowledgeNodes = knowledgeNodeMapper.getByCourseId(dto.getCourseId());
        List<AiInputKnowledgeNodeDTO> knowledgeDTOS = knowledgeNodeConverter.knowledgeNodesToAiDTOs(knowledgeNodes);
        String knowledgeJson = convertListToJson(knowledgeDTOS);
        // 1. 创建 SSE 信号汇
        Sinks.Many<AiStreamPayload> sink = Sinks.many().unicast().onBackpressureBuffer();
        var converter = new BeanOutputConverter<>(ExamGenCriteria.class);
        String formatInstructions = converter.getFormat();
        // 构造context
        ExamGenerationContext context = ExamGenerationContext.builder()
                .formatInstructions(formatInstructions)
                .courseId(dto.getCourseId())
                .userPrompt(dto.getPrompt())
                .knowledgeJson(knowledgeJson)
                .selectedQuestionJson(examSessionDTO.getSelectedQuestionJson())
                .context(examSessionDTO.getContext())
                .build();
        // 获取并转换流
        StringBuilder jsonBuffer = new StringBuilder();
        Flux<AiStreamPayload> aiResponseFlux = examAiAgent.callAiStream(context, sink)
                .flatMap(response -> processStreamResponse(response, jsonBuffer));
        // 最后一步
        Mono<AiStreamPayload> finalAction = Mono.fromCallable(() -> {
            String fullJson = jsonBuffer.toString();
            // 直接用 converter 转换！
            ExamGenCriteria criteria = converter.convert(fullJson);
            List<QuestionQueryVO> vos = questionService.getBatch(criteria.getIds());
            List<SelectedQuestionItemDTO> dtos = questionConverter.questionQueryVosToSelectedItems(vos);
            // 存入redis
            String idsJson = convertIdListToJson(dtos);
            examSessionRedisRepository.save(dto.getSessionId(), new ExamSessionDTO(criteria.getContext(), idsJson));
            return new AiStreamPayload(FINISH_EVENT, vos);
        });
        // 链接flux和momo
        return Flux.concat(aiResponseFlux, finalAction)
                .onErrorResume(e -> {
                    log.error("流处理异常", e);
                    AiStreamPayload errorPayload = new AiStreamPayload(ERROR_EVENT, "生成失败，请重试");
                    return Flux.just(errorPayload);
                });
    }

    private Flux<AiStreamPayload> processStreamResponse(ChatResponse response, StringBuilder buffer) {
        var output = response.getResult().getOutput();

        // A. 提取思考过程
        Object reasoning = output.getMetadata().get("reasoningContent");
        if (reasoning != null && !reasoning.toString().isEmpty()) {
            return Flux.just(new AiStreamPayload(GENERATING_EVENT, reasoning.toString()));
        }
        // B. 提取正文 (JSON) 并缓存，不直接推给前端
        String content = output.getText();
        if (content != null) {
            buffer.append(content);
        }
        return Flux.empty();
    }

    private void getQuestionDetailsBatch(List<StudentExamQuestionVO> questionQueryVOS, int type) {
        if (questionQueryVOS == null || questionQueryVOS.isEmpty()) {
            return;
        }

        List<Long> optionIds = new ArrayList<>();
        List<Long> blankIds = new ArrayList<>();
        List<Long> shortIds = new ArrayList<>();

        for (StudentExamQuestionVO questionVO : questionQueryVOS) {
            if (type == 1 || type == 2) {
                optionIds.add(questionVO.getQuestionId());
            } else if (type == 3) {
                blankIds.add(questionVO.getQuestionId());
            } else if (type == 4) {
                shortIds.add(questionVO.getQuestionId());
            }
        }

        Map<Long, List<QuestionOption>> optionMap = new HashMap<>();
        Map<Long, List<QuestionFillBlank>> blankMap = new HashMap<>();
        Map<Long, QuestionShortAnswer> shortMap = new HashMap<>();

        if (!optionIds.isEmpty()) {
            List<QuestionOption> options = questionOptionMapper.selectByQuestionIds(optionIds);
            for (QuestionOption o : options) {
                optionMap.computeIfAbsent(o.getQuestionId(), k -> new ArrayList<>()).add(o);
            }
        }

        if (!blankIds.isEmpty()) {
            List<QuestionFillBlank> blanks = questionFillBlankMapper.selectByQuestionIds(blankIds);
            for (QuestionFillBlank b : blanks) {
                blankMap.computeIfAbsent(b.getQuestionId(), k -> new ArrayList<>()).add(b);
            }
        }

        if (!shortIds.isEmpty()) {
            List<QuestionShortAnswer> answers = questionShortAnswerMapper.selectByQuestionIds(shortIds);
            for (QuestionShortAnswer shortAnswer : answers) {
                shortMap.put(shortAnswer.getQuestionId(), shortAnswer);
            }
        }

        for (StudentExamQuestionVO questionVO : questionQueryVOS) {
            Long questionId = questionVO.getQuestionId();

            if (type == 1 || type == 2) {
                List<QuestionOption> options = optionMap.getOrDefault(questionId, Collections.emptyList());
                ObjectNode node = objectMapper.createObjectNode();
                node.set(OPTIONS, objectMapper.valueToTree(options));
                questionVO.setDetails(node);
            } else if (type == 3) {
                List<QuestionFillBlank> blanks = blankMap.getOrDefault(questionId, Collections.emptyList());
                ObjectNode node = objectMapper.createObjectNode();
                node.set(BLANKS, objectMapper.valueToTree(blanks));
                questionVO.setDetails(node);
            } else if (type == 4) {
                QuestionShortAnswer shortAnswer = shortMap.get(questionId);
                questionVO.setDetails(objectMapper.valueToTree(shortAnswer));
            }
        }
    }


    /**
     * @param studentGetExamDTO
     * @return
     */
    @Override
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
            if (vo.getQuestionType().equals("fill_blank") && studentExamFillBlankQuestionVOS != null) {
                for (StudentExamFillBlankQuestionVO studentExamFillBlankQuestionVO : studentExamFillBlankQuestionVOS) {
                    if (questionIds.contains(studentExamFillBlankQuestionVO.getQuestionId())) {
                        questions.add(studentExamFillBlankQuestionVO);
                    }
                }
            } else if (vo.getQuestionType().equals("short_answer") && studentExamFillBlankQuestionVOS != null) {
                for (StudentExamShortAnswerQuestionVO studentExamShortAnswerQuestionVO : studentExamShortAnswerQuestionVOS) {
                    if (questionIds.contains(studentExamShortAnswerQuestionVO.getQuestionId())) {
                        questions.add(studentExamShortAnswerQuestionVO);
                    }
                }
            } else if (studentExamFillBlankQuestionVOS != null) {
                for (StudentExamChoiceQuestionVO studentExamChoiceQuestionVO : studentExamChoiceQuestionVOS) {
                    if (questionIds.contains(studentExamChoiceQuestionVO.getQuestionId())) {
                        questions.add(studentExamChoiceQuestionVO);
                        studentExamSectionVO.setChoiceScore(studentExamChoiceQuestionVO.getScore());
                    }
                }
            }
            String type = String.valueOf(studentExamSectionVO.getQuestionType());
            int t = switch (type) {
                case "SINGLE" -> 1;
                case "MULTIPLE" -> 2;
                case "FILL_BLANK" -> 3;
                case "SHORT_ANSWER" -> 4;
                default -> 0;
            };
            getQuestionDetailsBatch(questions, t);
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
    public List<GradesVO> getGrades(Long examId, Long classId) {
        return examScoreMapper.getGrades(examId, classId);
    }
}
