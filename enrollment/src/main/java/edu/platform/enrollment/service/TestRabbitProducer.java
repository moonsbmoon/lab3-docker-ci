package edu.platform.enrollment.service;

import edu.platform.enrollment.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestRabbitProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendTestEvent(Long enrollmentId, Long courseId, Long userId) {
        String eventId = UUID.randomUUID().toString();
        // Формируем JSON вручную для теста
        String payload = String.format(
                "{\"eventId\":\"%s\", \"enrollmentId\":%d, \"courseId\":%d, \"userId\":%d}",
                eventId, enrollmentId, courseId, userId
        );

        System.out.println("Отправка события: " + payload);

        // Отправляем
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                payload
        );
    }
}