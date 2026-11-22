package edu.platform.enrollment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saga_states")
@Getter @Setter @NoArgsConstructor
public class SagaInstance {
    @Id
    private UUID id;
    private Long enrollmentId;

    @Enumerated(EnumType.STRING)
    private SagaState currentState;

    private UUID commandId;
    private Instant updatedAt = Instant.now();

    public SagaInstance(Long enrollmentId) {
        this.id = UUID.randomUUID();
        this.enrollmentId = enrollmentId;
        this.currentState = SagaState.NEW;
    }
}