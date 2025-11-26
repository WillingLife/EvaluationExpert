package com.smartcourse.infra.rabbitmq;

/**
 * Central place for RabbitMQ exchange, queue, and routing key names used by task processing.
 */
public final class RabbitTaskConstants {

    private RabbitTaskConstants() {
    }

    public static final String TASK_EXCHANGE = "task.direct";

    public static final String GRADE_SHORT_QUESTION_QUEUE = "task.grade-short-question.queue";

    public static final String GRADE_SHORT_QUESTION_ROUTING_KEY = "task.grade-short-question";

    public static final String MAPPING_KNOWLEDGE_QUEUE = "task.mapping-knowledge.queue";

    public static final String MAPPING_KNOWLEDGE_ROUTING_KEY = "task.mapping-knowledge";

    public static final String GRADE_ASSIGNMENT_QUEUE = "task.grade-assignment.queue";
    public static final String GRADE_ASSIGNMENT_ROUTING_KEY = "task.grade-assignment";
}
