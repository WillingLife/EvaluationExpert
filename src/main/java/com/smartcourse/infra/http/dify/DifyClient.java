package com.smartcourse.infra.http.dify;

import com.smartcourse.pojo.dto.dify.DifyCompletionResponse;
import com.smartcourse.pojo.dto.dify.DifyGradeShortQuestionDTO;
import com.smartcourse.pojo.dto.dify.DifyGradeQuestionResponse;
import com.smartcourse.pojo.dto.dify.DifyRequestBaseDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface DifyClient {
    @PostExchange()
    DifyCompletionResponse<DifyGradeQuestionResponse> gradeShortQuestion(
            @RequestBody DifyRequestBaseDTO<DifyGradeShortQuestionDTO> dto);
}
