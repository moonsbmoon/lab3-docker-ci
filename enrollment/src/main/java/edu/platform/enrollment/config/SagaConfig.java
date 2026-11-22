package edu.platform.enrollment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String REPLY_QUEUE = "saga.reply.queue";
    public static final String REPLY_ROUTING_KEY = "saga.reply";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE, true, false);
    }

    @Bean
    public Queue sagaReplyQueue() {
        return QueueBuilder.durable(REPLY_QUEUE).build();
    }

    @Bean
    public Binding sagaReplyBinding(Queue sagaReplyQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(sagaReplyQueue).to(sagaExchange).with(REPLY_ROUTING_KEY);
    }
}