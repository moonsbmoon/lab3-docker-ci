package edu.platform.payment.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.platform.payment.config.PaymentRabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCommandListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = PaymentRabbitConfig.PAYMENT_QUEUE)
    public void handlePaymentCommand(Message message) {
        try {
            String payload = new String(message.getBody());
            JsonNode json = objectMapper.readTree(payload);

            String commandId = json.get("commandId").asText();
            String sagaId = json.get("sagaId").asText();
            Long userId = json.get("userId").asLong();

            log.info("Получена команда PROCESS_PAYMENT: commandId={}, sagaId={}", commandId, sagaId);

            // TODO: Для тестов отказа вы можете временно поменять эту переменную на "FAILED"
            // В реальной системе здесь был бы вызов платежного шлюза
            String paymentStatus = "SUCCESS";

            // Задержка для имитации (Задание 6)
            // Thread.sleep(5000);

            String replyPayload = String.format(
                    "{\"commandId\":\"%s\", \"sagaId\":\"%s\", \"status\":\"%s\"}",
                    commandId, sagaId, paymentStatus.equals("SUCCESS") ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED"
            );

            rabbitTemplate.convertAndSend(
                    PaymentRabbitConfig.SAGA_EXCHANGE,
                    PaymentRabbitConfig.REPLY_ROUTING_KEY,
                    replyPayload
            );

            log.info("Ответ {} отправлен для sagaId={}", paymentStatus, sagaId);

        } catch (Exception e) {
            log.error("Ошибка при обработке оплаты: {}", e.getMessage());
        }
    }
}