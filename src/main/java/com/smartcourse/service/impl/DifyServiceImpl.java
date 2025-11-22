package com.smartcourse.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcourse.constant.DifyEventConstant;
import com.smartcourse.converter.KnowledgeNodeConverter;
import com.smartcourse.converter.QuestionKnowledgeDocumentConverter;
import com.smartcourse.exception.DifyServiceException;
import com.smartcourse.infra.http.dify.DifyClientGateway;
import com.smartcourse.mapper.KnowledgeNodeMapper;
import com.smartcourse.model.QuestionKnowledgeDocument;
import com.smartcourse.pojo.dto.dify.*;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyCompletionResponse;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyRequestBaseDTO;
import com.smartcourse.pojo.dto.dify.base.streaming.DifyStreamEvent;
import com.smartcourse.pojo.dto.dify.exam.DifyExamGenQueryDTO;
import com.smartcourse.pojo.entity.KnowledgeNode;
import com.smartcourse.pojo.vo.dify.DifyMappingKnowledgeVO;
import com.smartcourse.repository.QuestionKnowledgeRepository;
import com.smartcourse.service.DifyService;
import com.smartcourse.utils.DifyStreamHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DifyServiceImpl implements DifyService {
    private final ObjectMapper objectMapper;
    private final KnowledgeNodeMapper knowledgeNodeMapper;
    private final KnowledgeNodeConverter knowledgeNodeConverter;
    private final DifyClientGateway difyClientGateway;
    private final QuestionKnowledgeDocumentConverter questionKnowledgeDocumentConverter;
    private final QuestionKnowledgeRepository questionKnowledgeRepository;
    private final DifyStreamHelper difyStreamHelper;


    @Override
    public BigDecimal gradeShortQuestion(DifyGradeShortQuestionDTO dto) {
        return null;
    }

    @Override
    public void mappingKnowledge(Long courseId, Long questionId, String question) {
        //  构造调用dify的dto
        List<KnowledgeNode> knowledgeNodes = knowledgeNodeMapper.getByCourseId(courseId);
        List<DifyKnowledgeNodeDTO> knowledgeDTOS = knowledgeNodeConverter.knowledgeNodesToDifyDTOs(knowledgeNodes);
        String knowledgeJson = convertListToJson(knowledgeDTOS);
        DifyMappingKnowledgeDTO dto = new DifyMappingKnowledgeDTO(question, knowledgeJson);
        log.info("发送消息");

        // 远程调用dify
        DifyCompletionResponse<DifyMappingKnowledgeVO> vo = difyClientGateway.mappingKnowledgeClient()
                .mappingKnowledge(new DifyRequestBaseDTO<>("user", dto));

        log.info("调用成功");

        // insert into Es
        QuestionKnowledgeDocument document = questionKnowledgeDocumentConverter.difyKnowledgeVoToKnowledgeDocument(
                vo.getData().getOutputs(), questionId, courseId);
        questionKnowledgeRepository.save(document);
        log.info("insert success");
    }

    private String convertListToJson(List<DifyKnowledgeNodeDTO> knowledgeDTOS) {
        try {
            return objectMapper.writeValueAsString(knowledgeDTOS);

        } catch (JsonProcessingException e) {
            // 处理转换异常
            log.error(e.getMessage());
            throw new RuntimeException("序列化失败", e);
        }
    }

    @Override
    public Flux<DifyStreamEvent> processGenerateQuery(DifyExamGenQueryDTO dto) {
        Flux<String> stringFlux = difyClientGateway.examGenerateQueryClient().examGenerateQuery(
                new DifyRequestBaseDTO<>("user", dto, "streaming"));

        Flux<DifyStreamEvent> eventFlux = difyStreamHelper.filterEvents(stringFlux,DifyEventConstant.TEXT_CHUNK_EVENT,DifyEventConstant.WORKFLOW_FINISHED_EVENT );
        return eventFlux
                .timeout(Duration.ofSeconds(30))
                .retry(1)
                .onErrorMap(e-> new DifyServiceException(e.getMessage()));
    }
}

