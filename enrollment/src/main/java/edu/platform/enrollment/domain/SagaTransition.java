package edu.platform.enrollment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saga_transitions")
@Getter @Setter @NoArgsConstructor
public class SagaTransition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID sagaId;

    @Enumerated(EnumType.STRING)
    private SagaState fromState;

    @Enumerated(EnumType.STRING)
    private SagaState toState;

    private Instant createdAt = Instant.now();

    public SagaTransition(UUID sagaId, SagaState fromState, SagaState toState) {
        this.sagaId = sagaId;
        this.fromState = fromState;
        this.toState = toState;
    }
}