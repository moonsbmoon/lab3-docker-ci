package edu.platform.billing.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import edu.platform.billing.domain.InboxEvent;
import edu.platform.billing.domain.InboxEventId;
import edu.platform.billing.domain.Subscription;
import edu.platform.billing.repository.InboxRepository;
import edu.platform.billing.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentCreatedListener {

    private final InboxRepository inboxRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;

    private static final String HANDLER_ID = "subscription-activator";

    @RabbitListener(queues = "enrollment.created.queue")
    @Transactional
    public void handleEnrollmentCreated(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String payload = new String(message.getBody());

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            UUID eventId = UUID.fromString(jsonNode.get("eventId").asText());
            Long userId = jsonNode.get("userId").asLong();

            InboxEventId inboxId = new InboxEventId(eventId, HANDLER_ID);
            if (inboxRepository.existsById(inboxId)) {
                log.info("Событие {} уже обработано (Inbox). Пропускаем.", eventId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            log.info("Активация подписки для userId={}", userId);
            Subscription sub = subscriptionRepository.findById(userId).orElse(new Subscription(userId, "FREE"));
            sub.setTariff("PREMIUM");
            sub.setActivationEventId(eventId);
            subscriptionRepository.save(sub);

            inboxRepository.save(new InboxEvent(eventId, HANDLER_ID));
            channel.basicAck(deliveryTag, false);
            log.info("Событие {} успешно обработано и подтверждено (ack).", eventId);

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения: {}", e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}