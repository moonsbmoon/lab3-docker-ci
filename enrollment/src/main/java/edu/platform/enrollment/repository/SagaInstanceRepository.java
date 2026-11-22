package edu.platform.enrollment.repository;

import edu.platform.enrollment.domain.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {
}