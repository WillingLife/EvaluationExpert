package com.smartcourse.service.impl;

import com.smartcourse.infra.rabbitmq.TaskProducer;
import com.smartcourse.mapper.QuestionMapper;
import com.smartcourse.pojo.entity.Question;
import com.smartcourse.service.DevService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevServiceImpl implements DevService {
    private final QuestionMapper questionMapper;
    private final TaskProducer taskProducer;
    @Override
    public void updateQuestionKnowledge(Long courseId, Long fromQuestionId, Long toQuestionId) {
        for(long i=fromQuestionId;i<=toQuestionId;i++){
            Question question = questionMapper.selectById(i);
            taskProducer.publishMappingKnowledgeTask(i,courseId,question.getStem());
        }

    }
}
