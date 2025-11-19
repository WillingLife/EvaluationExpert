package com.smartcourse.infra.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 声明用于任务处理的RabbitMQ资源和转换器。
 */
@EnableRabbit
@Configuration
public class RabbitTaskConfiguration {

    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(RabbitTaskConstants.TASK_EXCHANGE);
    }

    @Bean
    public Queue gradeShortQuestionQueue() {
        return QueueBuilder.durable(RabbitTaskConstants.GRADE_SHORT_QUESTION_QUEUE).build();
    }

    @Bean
    public Binding gradeShortQuestionBinding(DirectExchange taskExchange, Queue gradeShortQuestionQueue) {
        return BindingBuilder
                .bind(gradeShortQuestionQueue)
                .to(taskExchange)
                .with(RabbitTaskConstants.GRADE_SHORT_QUESTION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
