package com.smartcourse.infra.http.dify;

import com.smartcourse.pojo.dto.dify.*;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyCompletionResponse;
import com.smartcourse.pojo.dto.dify.base.blocked.DifyRequestBaseDTO;
import com.smartcourse.pojo.dto.dify.exam.DifyExamGenQueryDTO;
import com.smartcourse.pojo.vo.dify.DifyMappingKnowledgeVO;
import com.smartcourse.pojo.vo.dify.DifyPolishAssignmentVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;

public interface DifyClient {
    @PostExchange()
    DifyCompletionResponse<DifyGradeQuestionResponse> gradeShortQuestion(
            @RequestBody DifyRequestBaseDTO<DifyGradeShortQuestionDTO> dto);

    @PostExchange
    DifyCompletionResponse<DifyPolishAssignmentVO>  polishAssignment(
            @RequestBody DifyRequestBaseDTO<DifyPolishAssignmentDTO> dto);

    @PostExchange
    DifyCompletionResponse<DifyMappingKnowledgeVO> mappingKnowledge(
            @RequestBody DifyRequestBaseDTO<DifyMappingKnowledgeDTO> dto);

    @PostExchange
    Flux<String> examGenerateQuery(@RequestBody DifyRequestBaseDTO<DifyExamGenQueryDTO> dto);


}
