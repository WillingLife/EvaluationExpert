package com.smartcourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.converter.ExamConverter;
import com.smartcourse.converter.ExamScoreItemConverter;
import com.smartcourse.converter.KnowledgeNodeConverter;
import com.smartcourse.converter.QuestionConverter;
import com.smartcourse.enums.ExamStatusEnum;
import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.exception.SqlErrorException;
import com.smartcourse.infra.ai.ExamAiAgent;
import com.smartcourse.infra.ai.ExamGenerationContext;
import com.smartcourse.infra.ai.LlmKernelService;
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
import com.smartcourse.pojo.vo.exam.items.TeacherGetExamItemVO;
import com.smartcourse.service.AiToolService;
import com.smartcourse.service.QuestionService;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherExamServiceImpl implements TeacherExamService {

    private final ExamConverter examConverter;
    private final ExamMapper examMapper;
    private final ExamSectionMapper examSectionMapper;
    private final ExamItemMapper examItemMapper;
    private final ExamClassMapper examClassMapper;
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

    private static final String GENERATING_EVENT = "generating";
    private static final String FINISH_EVENT = "finish";
    private static final String ERROR_EVENT  = "error";





    @Override
    @Transactional
    public Long saveExam(TeacherSaveExamDTO teacherSaveExamDTO) {
        Exam exam = examConverter.teacherSaveExamDTOToExam(teacherSaveExamDTO);
        if (exam.getId() == null) {
            if (examMapper.insertExam(exam) < 1) {
                throw new SqlErrorException("数据库操作失败");
            }
            exam.batchUpdateExamIdIntoSections();
            examSectionMapper.insetSections(exam.getSections());
            exam.batchUpdateSectionIdIntoExamItems();
            examItemMapper.insertExamItemsByExamSections(exam.getSections());
        } else {
            examMapper.updateExamRecursive(exam);
        }


        return 0L;
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
        examScoreItemMapper.batchUpdateExamScoreItemSelectiveByScoreIdAndExamItemId(items);
    }

    @Override
    public TeacherViewAnswerVO viewStudentAnswers(TeacherViewAnswerDTO teacherViewAnswerDTO) {
        List<TeacherViewAnswerItemVO> studentAnswers = examScoreMapper.getStudentAnswers(teacherViewAnswerDTO.getExamId(),
                teacherViewAnswerDTO.getStudentId());
        return new TeacherViewAnswerVO(studentAnswers);
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
    private String convertIdListToJson(List<SelectedQuestionItemDTO> ids){
        try {
            return objectMapper.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("序列化失败", e);
        }
    }

    @Override
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
        ExamGenerationContext context= ExamGenerationContext.builder()
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
            examSessionRedisRepository.save(dto.getSessionId(),new ExamSessionDTO(criteria.getContext(),idsJson));
            return new AiStreamPayload(FINISH_EVENT,vos);
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

}
