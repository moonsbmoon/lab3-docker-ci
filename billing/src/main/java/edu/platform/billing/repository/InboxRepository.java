package edu.platform.billing.repository;

import edu.platform.billing.domain.InboxEvent;
import edu.platform.billing.domain.InboxEventId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<InboxEvent, InboxEventId> {
}