package com.smartcourse.service.impl;

import com.smartcourse.exception.IllegalOperationException;
import com.smartcourse.infra.redis.QuestionQueryESCacheRepository;
import com.smartcourse.pojo.dto.question.QuestionElasticSearchQueryDTO;
import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;
import com.smartcourse.pojo.vo.question.QuestionQueryESVO;
import com.smartcourse.service.ElasticSearchQueryService;
import com.smartcourse.service.QuestionAdvanceService;
import com.smartcourse.service.QuestionElasticSearchService;
import com.smartcourse.utils.Md5Calculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionAdvanceServiceImpl implements QuestionAdvanceService {

    private final ElasticSearchQueryService elasticSearchQueryService;
    private final QuestionQueryESCacheRepository questionQueryESCacheRepository;

    @Override
    public QuestionQueryESVO queryAdvance(QuestionElasticSearchQueryDTO dto) {
        String md5 = Md5Calculator.calculateQuestionESDtoMd5(dto);
        List<QuestionQueryESItemVO> questionQueryESItemVOS = questionQueryESCacheRepository.get(md5);
        if (questionQueryESItemVOS.isEmpty()) {
            // not hit
            questionQueryESItemVOS = elasticSearchQueryService.queryQuestionDocument(dto);
            questionQueryESCacheRepository.save(md5, questionQueryESItemVOS);
        }

        int fromIndex = (dto.getPageNumber() - 1) * 10;
        if (fromIndex >= questionQueryESItemVOS.size()) {
            throw new IllegalOperationException("无数据");
        }
        int toIndex = Math.min(dto.getPageNumber() * 10, questionQueryESItemVOS.size());
        Boolean hasMore = toIndex < questionQueryESItemVOS.size();
        ArrayList<QuestionQueryESItemVO> itemVOS = new ArrayList<>(questionQueryESItemVOS.subList(fromIndex, toIndex));

        return new QuestionQueryESVO(itemVOS, hasMore,questionQueryESItemVOS.size());
    }
}
