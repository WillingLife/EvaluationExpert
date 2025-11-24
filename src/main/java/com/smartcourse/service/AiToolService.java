package com.smartcourse.service;

import com.smartcourse.converter.AiToolConverter;
import com.smartcourse.infra.ai.dto.AiQueryQuestionDTO;
import com.smartcourse.infra.ai.vo.AiQueryQuestionVO;
import com.smartcourse.infra.ai.vo.AiQueryQuestionVOItem;
import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface AiToolService {
    AiQueryQuestionVO queryQuestionTool(AiQueryQuestionDTO request, Consumer<String> notifier);
}
