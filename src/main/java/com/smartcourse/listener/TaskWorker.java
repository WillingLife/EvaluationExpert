package com.smartcourse.listener;

import com.smartcourse.infra.rabbitmq.GradeShortQuestionTaskMessage;
import com.smartcourse.infra.rabbitmq.RabbitTaskConstants;
import com.smartcourse.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 监听RabbitMQ任务。随着新任务的引入，添加更多处理程序方法。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskWorker {
    private final GradeService gradeService;

    @RabbitListener(queues = RabbitTaskConstants.GRADE_SHORT_QUESTION_QUEUE)
    public void handleGradeShortQuestionTask(GradeShortQuestionTaskMessage message) {
        log.info(
                "Received gradeShortQuestion task with scoreId={} examItemId={}",
                message.getScoreId(),
                message.getExamItemId()
        );
        gradeService.gradeShortQuestion(message.getScoreId(), message.getExamItemId());
    }
}
