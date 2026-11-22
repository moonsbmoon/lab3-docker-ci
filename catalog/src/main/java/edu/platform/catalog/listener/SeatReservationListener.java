package edu.platform.catalog.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.catalog.config.SagaRabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatReservationListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = SagaRabbitConfig.RESERVE_QUEUE)
    public void handleReserveCommand(Message message) {
        try {
            String payload = new String(message.getBody());
            JsonNode json = objectMapper.readTree(payload);

            String commandId = json.get("commandId").asText();
            String sagaId = json.get("sagaId").asText();
            Long courseId = json.get("courseId").asLong();

            log.info("Получена команда RESERVE_SEAT: commandId={}, courseId={}", commandId, courseId);

            // TODO: Здесь в реальности мы бы проверяли наличие мест в БД (available_seats > 0)
            // Для лабы мы просто имитируем успешное резервирование.
            // Если места кончились, мы бы отправили "REJECTED"

            // Формируем ответ
            String replyPayload = String.format(
                    "{\"commandId\":\"%s\", \"sagaId\":\"%s\", \"status\":\"RESERVED\"}",
                    commandId, sagaId
            );

            // Отправляем ответ обратно в enrollment
            rabbitTemplate.convertAndSend(
                    SagaRabbitConfig.SAGA_EXCHANGE,
                    SagaRabbitConfig.REPLY_ROUTING_KEY,
                    replyPayload
            );

            log.info("Ответ RESERVED отправлен для sagaId={}", sagaId);

        } catch (Exception e) {
            log.error("Ошибка при обработке команды резервирования: {}", e.getMessage());
        }
    }
}