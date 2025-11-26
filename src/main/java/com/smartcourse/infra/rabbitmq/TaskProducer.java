package com.smartcourse.infra.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 发送RabbitMQ任务。新的任务类型可以作为附加方法添加。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishGradeShortQuestionTask(Long scoreId, Long examItemId) {
        GradeShortQuestionTaskMessage payload = GradeShortQuestionTaskMessage.builder()
                .scoreId(scoreId)
                .examItemId(examItemId)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitTaskConstants.TASK_EXCHANGE,
                RabbitTaskConstants.GRADE_SHORT_QUESTION_ROUTING_KEY,
                payload
        );
        log.info("Published gradeShortQuestion task with scoreId={} examItemId={}", scoreId, examItemId);
    }

    public void publishMappingKnowledgeTask(Long questionId, Long courseId, String question) {
        MappingKnowledgeTaskMessage payload = MappingKnowledgeTaskMessage.builder()
                .questionId(questionId)
                .courseId(courseId)
                .question(question)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitTaskConstants.TASK_EXCHANGE,
                RabbitTaskConstants.MAPPING_KNOWLEDGE_ROUTING_KEY,
                payload
        );
        log.info("Published mappingKnowledge task with questionId={} courseId={}", questionId, courseId);
    }
    public void publishGradeAssignmentTask(Long assignmentScoreId) {
        GradeAssignmentTaskMessage payload = GradeAssignmentTaskMessage.builder()
                .assignmentScoreId(assignmentScoreId)
                .build();
        rabbitTemplate.convertAndSend(
                RabbitTaskConstants.TASK_EXCHANGE,
                RabbitTaskConstants.GRADE_ASSIGNMENT_ROUTING_KEY,
                payload
        );
        log.info("Published grade assignment task with assignmentScoreId={}", assignmentScoreId);
    }
}
