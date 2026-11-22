package edu.platform.enrollment.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.enrollment.config.RabbitMQConfig;
import edu.platform.enrollment.domain.*;
import edu.platform.enrollment.repository.EnrollmentRepository;
import edu.platform.enrollment.repository.OutboxRepository;
import edu.platform.enrollment.repository.SagaInstanceRepository;
import edu.platform.enrollment.repository.SagaTransitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaReplyListener {

    private final SagaInstanceRepository sagaRepository;
    private final SagaTransitionRepository transitionRepository;
    private final OutboxRepository outboxRepository;
    private final EnrollmentRepository enrollmentRepository; // <-- ДОБАВЛЕНО
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "saga.reply.queue")
    @Transactional
    public void handleSagaReply(Message message) {
        try {
            String payload = new String(message.getBody());
            JsonNode json = objectMapper.readTree(payload);

            String sagaIdStr = json.get("sagaId").asText();
            String status = json.get("status").asText(); // RESERVED, PAYMENT_SUCCESS, PAYMENT_FAILED
            UUID sagaId = UUID.fromString(sagaIdStr);

            SagaInstance saga = sagaRepository.findById(sagaId).orElse(null);
            if (saga == null) return;

            // --- ЭТАП 1: ОТВЕТ ОТ CATALOG (Место зарезервировано) ---
            if ("RESERVED".equals(status) && saga.getCurrentState() == SagaState.RESERVING) {
                log.info("Место зарезервировано. Переходим к оплате для sagaId={}", sagaId);

                saga.setCurrentState(SagaState.DEBITING);
                sagaRepository.save(saga);
                transitionRepository.save(new SagaTransition(saga.getId(), SagaState.RESERVING, SagaState.DEBITING));

                UUID commandId = UUID.randomUUID();
                String paymentPayload = String.format(
                        "{\"commandId\":\"%s\", \"sagaId\":\"%s\", \"userId\":%d, \"action\":\"PROCESS_PAYMENT\"}",
                        commandId, saga.getId(), 555L
                );
                outboxRepository.save(new OutboxEvent(saga.getEnrollmentId(), "PaymentCommand", paymentPayload));
            }

            // --- ЭТАП 2: ОТВЕТ ОТ PAYMENT (Оплата прошла успешно) ---
            else if ("PAYMENT_SUCCESS".equals(status) && saga.getCurrentState() == SagaState.DEBITING) {
                log.info("Оплата прошла успешно. Подтверждаем запись для sagaId={}", sagaId);

                // 1. Переводим Сагу в CONFIRMED
                saga.setCurrentState(SagaState.CONFIRMED);
                sagaRepository.save(saga);
                transitionRepository.save(new SagaTransition(saga.getId(), SagaState.DEBITING, SagaState.CONFIRMED));

                // 2. Обновляем статус самой записи (если у Enrollment есть поле status)
                // Enrollment enrollment = enrollmentRepository.findById(saga.getEnrollmentId()).orElse(null);
                // if (enrollment != null) { enrollment.setStatus("ACTIVE"); enrollmentRepository.save(enrollment); }

                // 3. (Задание 12) Отправляем событие EnrollmentConfirmed в Outbox
                String eventPayload = String.format(
                        "{\"sagaId\":\"%s\", \"enrollmentId\":%d, \"userId\":555}",
                        saga.getId(), saga.getEnrollmentId()
                );
                outboxRepository.save(new OutboxEvent(saga.getEnrollmentId(), "EnrollmentConfirmed", eventPayload));
                log.info("Событие EnrollmentConfirmed добавлено в Outbox");
            }

            // --- ЭТАП 3: ОТВЕТ ОТ PAYMENT (Оплата отклонена) ---
            else if ("PAYMENT_FAILED".equals(status) && saga.getCurrentState() == SagaState.DEBITING) {
                log.info("Оплата отклонена. Запускаем компенсацию для sagaId={}", sagaId);

                // 1. Переводим Сагу в COMPENSATING
                saga.setCurrentState(SagaState.COMPENSATING);
                sagaRepository.save(saga);
                transitionRepository.save(new SagaTransition(saga.getId(), SagaState.DEBITING, SagaState.COMPENSATING));

                // 2. Отправляем команду на освобождение места (Компенсация)
                UUID commandId = UUID.randomUUID();
                String compensationPayload = String.format(
                        "{\"commandId\":\"%s\", \"sagaId\":\"%s\", \"courseId\":100, \"action\":\"RELEASE_SEAT\"}",
                        commandId, saga.getId()
                );
                outboxRepository.save(new OutboxEvent(saga.getEnrollmentId(), "ReleaseSeatCommand", compensationPayload));
                log.info("Команда ReleaseSeatCommand добавлена в Outbox для компенсации");
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке ответа Саги: {}", e.getMessage());
        }
    }
}