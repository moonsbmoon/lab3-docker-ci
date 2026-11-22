package edu.platform.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentRabbitConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";
    public static final String PAYMENT_QUEUE = "payment.process.queue";
    public static final String PAYMENT_ROUTING_KEY = "payment.process";

    public static final String REPLY_ROUTING_KEY = "saga.reply";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(paymentQueue).to(sagaExchange).with(PAYMENT_ROUTING_KEY);
    }
}