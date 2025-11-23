package com.smartcourse.service.impl;

import com.smartcourse.converter.AiToolConverter;
import com.smartcourse.infra.ai.dto.AiQueryQuestionDTO;
import com.smartcourse.infra.ai.vo.AiQueryQuestionVO;
import com.smartcourse.infra.ai.vo.AiQueryQuestionVOItem;
import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;
import com.smartcourse.service.AiToolService;
import com.smartcourse.service.ElasticSearchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AiToolServiceImpl implements AiToolService {
    private final ElasticSearchQueryService elasticSearchQueryService;
    private final AiToolConverter aiToolConverter;
    private static final int MAX_QUESTIONS = 75;

    @Override
    public AiQueryQuestionVO queryQuestionTool(AiQueryQuestionDTO request, Consumer<String> notifier) {
        try {
            notifier.accept("正在查询题库："+request.getQuery());
            QuestionElasticSearchQueryDTO dto = aiToolConverter.aiQueryDtoToEsQueryDto(request);
            List<QuestionQueryESItemVO> esItemVOS = elasticSearchQueryService.queryQuestionDocument(dto);
            List<AiQueryQuestionVOItem> questions = aiToolConverter.esQueryVoListToAiQueryVoLIst(esItemVOS);
            if (questions == null) {
                questions = new ArrayList<>();
            } else if (questions.size() > MAX_QUESTIONS) {
                questions = new ArrayList<>(questions.subList(0, MAX_QUESTIONS));
            }
            notifier.accept("查询题目完成，共查到"+questions.size()+"道题目");
            return new AiQueryQuestionVO(questions, "success");
        } catch (Exception e) {
            notifier.accept("查询失败");
            return new AiQueryQuestionVO(null, "fail");
        }
    }
}
