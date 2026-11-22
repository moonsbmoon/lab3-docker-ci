package edu.platform.catalog.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaRabbitConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String RESERVE_QUEUE = "catalog.reserve.queue";
    public static final String RESERVE_ROUTING_KEY = "catalog.reserve";

    public static final String REPLY_QUEUE = "saga.reply.queue";
    public static final String REPLY_ROUTING_KEY = "saga.reply";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE, true, false);
    }

    @Bean
    public Queue reserveQueue() {
        return QueueBuilder.durable(RESERVE_QUEUE).build();
    }

    @Bean
    public Binding reserveBinding(Queue reserveQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(reserveQueue).to(sagaExchange).with(RESERVE_ROUTING_KEY);
    }

    @Bean
    public Queue replyQueue() {
        return QueueBuilder.durable(REPLY_QUEUE).build();
    }

    @Bean
    public Binding replyBinding(Queue replyQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(replyQueue).to(sagaExchange).with(REPLY_ROUTING_KEY);
    }
}