package edu.platform.billing.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @Id
    private Long userId;

    private String tariff;

    private Instant updatedAt = Instant.now();

    @Column(name = "activation_event_id")
    private UUID activationEventId;

    public Subscription(Long userId, String tariff) {
        this.userId = userId;
        this.tariff = tariff;
    }
}