package edu.platform.enrollment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "enrollment.exchange";
    public static final String QUEUE_NAME = "enrollment.created.queue";
    public static final String ROUTING_KEY = "enrollment.created";

    // DLQ (Dead Letter Queue) для сообщений с ошибками
    public static final String DLX_EXCHANGE_NAME = "enrollment.dlx";
    public static final String DLQ_NAME = "enrollment.created.dlq";
    public static final String DLQ_ROUTING_KEY = "enrollment.dead";

    // 1. Основной Exchange (Topic)
    @Bean
    public TopicExchange enrollmentExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false); // durable = true
    }

    // 2. Основная очередь (Durable) + привязка к DLX
    @Bean
    public Queue enrollmentQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    // 3. Биндинг (связываем Exchange и Queue)
    @Bean
    public Binding enrollmentBinding(Queue enrollmentQueue, TopicExchange enrollmentExchange) {
        return BindingBuilder.bind(enrollmentQueue).to(enrollmentExchange).with(ROUTING_KEY);
    }

    // ================= НАСТРОЙКА DLQ =================

    // 4. Exchange для мертвых сообщений
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE_NAME);
    }

    // 5. Очередь для мертвых сообщений
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    // 6. Биндинг для DLQ
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_ROUTING_KEY);
    }
}