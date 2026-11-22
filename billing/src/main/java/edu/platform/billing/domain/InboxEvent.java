package edu.platform.billing.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox_events")
@IdClass(InboxEventId.class)
@Getter @Setter @NoArgsConstructor
public class InboxEvent {
    @Id
    private UUID eventId;
    @Id
    private String handlerId;
    private Instant processedAt = Instant.now();

    public InboxEvent(UUID eventId, String handlerId) {
        this.eventId = eventId;
        this.handlerId = handlerId;
    }
}