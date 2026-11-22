package edu.platform.enrollment.service;

import edu.platform.enrollment.config.RabbitMQConfig;
import edu.platform.enrollment.domain.OutboxEvent;
import edu.platform.enrollment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayService.class);

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    // Запускается каждую секунду
    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processOutbox() {
        // 1. Забираем пачку событий (с блокировкой SKIP LOCKED)
        List<OutboxEvent> events = outboxRepository.findPendingEventsWithLock();

        if (events.isEmpty()) {
            return;
        }

        log.info("Relay нашел {} событий для публикации", events.size());

        for (OutboxEvent event : events) {
            try {
                // 2. Создаем CorrelationData для получения подтверждения (Publisher Confirm)
                CorrelationData correlationData = new CorrelationData(event.getId().toString());

                // 3. ОПРЕДЕЛЯЕМ КУДА ОТПРАВЛЯТЬ
                String exchange;
                String routingKey;

                if ("ReserveSeatCommand".equals(event.getEventType())) {
                    exchange = "saga.exchange";
                    routingKey = "catalog.reserve";
                } else if ("PaymentCommand".equals(event.getEventType())) {
                    exchange = "saga.exchange";
                    routingKey = "payment.process";
                } else {
                    // Старая логика для EnrollmentCreated (ЛР4)
                    exchange = RabbitMQConfig.EXCHANGE_NAME;
                    routingKey = RabbitMQConfig.ROUTING_KEY;
                }

                // 4. Отправляем в RabbitMQ
                rabbitTemplate.convertAndSend(
                        exchange,
                        routingKey,
                        event.getPayload(),
                        correlationData
                );

                // 5. Ждем подтверждения от брокера (максимум 5 секунд)
                CorrelationData.Confirm confirm = correlationData.getFuture().get(5, TimeUnit.SECONDS);

                if (confirm.isAck()) {
                    // 6. Если брокер подтвердил — помечаем как PUBLISHED
                    event.setStatus("PUBLISHED");
                    outboxRepository.save(event);
                    log.info("Событие {} успешно опубликовано", event.getId());
                } else {
                    log.error("Брокер отклонил событие {}: {}", event.getId(), confirm.getReason());
                    // Оставляем статус PENDING, попробуем в следующий раз
                }

            } catch (Exception e) {
                log.error("Ошибка при отправке события {}: {}", event.getId(), e.getMessage());
                // Если RabbitMQ недоступен, оставляем статус PENDING
            }
        }
    }
}