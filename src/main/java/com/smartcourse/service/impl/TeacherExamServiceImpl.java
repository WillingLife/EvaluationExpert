package com.smartcourse.service.impl;

import com.smartcourse.converter.ExamConverter;
import com.smartcourse.converter.ExamScoreItemConverter;
import com.smartcourse.enums.ExamStatusEnum;
import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.exception.SqlErrorException;
import com.smartcourse.infra.ai.LlmKernelService;
import com.smartcourse.mapper.*;
import com.smartcourse.pojo.dto.*;
import com.smartcourse.pojo.dto.dify.base.streaming.DifyStreamEvent;
import com.smartcourse.pojo.dto.dify.exam.DifyExamGenQueryDTO;
import com.smartcourse.pojo.dto.exam.stream.ExamGenStreamPayload;
import com.smartcourse.pojo.dto.exam.stream.ExamGeneratingPayload;
import com.smartcourse.pojo.entity.Exam;
import com.smartcourse.pojo.entity.ExamScoreItem;
import com.smartcourse.pojo.vo.exam.TeacherGetExamVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerItemVO;
import com.smartcourse.pojo.vo.exam.TeacherViewAnswerVO;
import com.smartcourse.pojo.vo.exam.items.TeacherGetExamItemVO;
import com.smartcourse.service.DifyService;
import com.smartcourse.service.TeacherExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TeacherExamServiceImpl implements TeacherExamService {

    private final ExamConverter examConverter;
    private final ExamMapper examMapper;
    private final ExamSectionMapper examSectionMapper;
    private final ExamItemMapper examItemMapper;
    private final ExamClassMapper examClassMapper;
    private final ExamScoreItemMapper examScoreItemMapper;
    private final ExamScoreItemConverter examScoreItemConverter;
    private final ExamScoreMapper examScoreMapper;
    private final DifyService difyService;

    private final LlmKernelService llmKernelService;

    @Value("classpath:prompts/system-role.st")
    private Resource systemPromptResource;
    @Value("classpath:prompts/user-input.st")
    private Resource userPromptResource;



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
        if (teacherPublishExamDTO.getClassIds() != null && !teacherPublishExamDTO.getClassIds().isEmpty()) {
            examClassMapper.deleteExamClassesByExamId(exam.getId());
            examClassMapper.insertExamClasses(exam.getId(), teacherPublishExamDTO.getClassIds());
        }

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


    @Override
    public Flux<ChatResponse> aiGenerateExam() {
        // 仅仅表示需要这个对象，后续处理
        DifyExamGenQueryDTO dto = new DifyExamGenQueryDTO();
        // 第一次dify调用
//        Flux<DifyStreamEvent> difyFlux = difyService.processGenerateQuery(dto);
        PromptTemplate systemTemplate = new PromptTemplate(systemPromptResource);
        PromptTemplate userTemplate = new PromptTemplate(userPromptResource);

        // 3. 填充参数
        Map<String, Object> vars = Map.of(
                "context", "no");

        Map<String, Object> vars2 = Map.of(
                "user_prompt","",
                "selected_question_json","[]",
                "knowledge_json","[{id:1,name:test}]"
        );

        // 4. 生成最终 String
        String finalSystemPrompt = systemTemplate.render(vars);
        String finalUserPrompt = userTemplate.render(vars2);
        Flux<ChatResponse> chatResponseFlux = llmKernelService.streamChat(finalSystemPrompt, "finalUserPrompt");
        return chatResponseFlux;
    }


    /**
     * 通用转换器：将 Dify 原始流转换为前端 SSE 流
     *
     * @param rawStream     Dify 中层返回的原始流
     * @param stepName      当前步骤的名称 (用于 text_chunk)
     * @param finishHandler 结束时的回调逻辑 (输入是 finish 事件，输出是想要发送给前端的 SSE)
     */
    private Flux<ServerSentEvent<ExamGenStreamPayload>> transformToSSE(
            Flux<DifyStreamEvent> rawStream,
            String stepName,
            Function<DifyStreamEvent, Mono<ServerSentEvent<ExamGenStreamPayload>>> finishHandler) {

        return rawStream.flatMap(event -> {
            // --- 通用部分：处理打字机效果 ---
            if ("text_chunk".equals(event.getEvent())) {
                String text = event.getData().path("text").asText();
                return Flux.just(ServerSentEvent.<ExamGenStreamPayload>builder()
                        .event("generating")
                        .data(new ExamGeneratingPayload(stepName, text))
                        .build());
            }

            // --- 变化部分：处理结束事件 ---
            if ("workflow_finished".equals(event.getEvent())) {
                // 调用传入的回调函数，执行具体的业务逻辑
                return finishHandler.apply(event);
            }

            return Flux.empty();
        });
    }
}
