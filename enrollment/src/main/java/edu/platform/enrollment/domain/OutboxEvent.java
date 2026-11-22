package edu.platform.enrollment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter @Setter @NoArgsConstructor
public class OutboxEvent {
    @Id
    private UUID id;

    private Long aggregateId;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status;
    private Instant createdAt = Instant.now();

    public OutboxEvent(Long aggregateId, String eventType, String payload) {
        this.id = UUID.randomUUID(); // Генерируем ID события прямо здесь
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = "PENDING";
    }
}