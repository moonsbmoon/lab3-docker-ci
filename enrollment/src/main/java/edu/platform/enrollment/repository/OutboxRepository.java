package edu.platform.enrollment.repository;

import edu.platform.enrollment.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    // Аннотация @Lock УДАЛЕНА, так как для nativeQuery блокировка пишется прямо в SQL
    @Query(value = "SELECT * FROM outbox_events WHERE status = 'PENDING' ORDER BY created_at LIMIT 10 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findPendingEventsWithLock();
}