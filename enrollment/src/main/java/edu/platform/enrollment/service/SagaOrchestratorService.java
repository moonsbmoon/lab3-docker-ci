package edu.platform.enrollment.service;

import edu.platform.enrollment.domain.*;
import edu.platform.enrollment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaOrchestratorService {

    private final EnrollmentRepository enrollmentRepository;
    private final SagaInstanceRepository sagaRepository;
    private final SagaTransitionRepository transitionRepository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public String startPaidEnrollment(Long courseId, Long userId) {
        // 1. Создаем саму запись (пока просто PENDING)
        Enrollment enrollment = new Enrollment(courseId, userId);
        enrollmentRepository.save(enrollment);

        // 2. Создаем Сагу в состоянии NEW
        SagaInstance saga = new SagaInstance(enrollment.getId());
        sagaRepository.save(saga);
        transitionRepository.save(new SagaTransition(saga.getId(), null, SagaState.NEW));

        // 3. Переводим в RESERVING и создаем команду
        UUID commandId = UUID.randomUUID();
        saga.setCurrentState(SagaState.RESERVING);
        saga.setCommandId(commandId);
        sagaRepository.save(saga);
        transitionRepository.save(new SagaTransition(saga.getId(), SagaState.NEW, SagaState.RESERVING));

        // 4. Сохраняем команду в Outbox (в той же транзакции)
        String payload = String.format(
                "{\"commandId\":\"%s\", \"sagaId\":\"%s\", \"courseId\":%d, \"userId\":%d, \"action\":\"RESERVE_SEAT\"}",
                commandId, saga.getId(), courseId, userId
        );
        OutboxEvent outboxEvent = new OutboxEvent(enrollment.getId(), "ReserveSeatCommand", payload);
        outboxRepository.save(outboxEvent);

        return String.format("{\"sagaId\":\"%s\", \"status\":\"RESERVING\"}", saga.getId());
    }
}