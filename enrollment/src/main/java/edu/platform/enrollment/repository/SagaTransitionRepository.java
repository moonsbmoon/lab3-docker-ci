package edu.platform.enrollment.repository;

import edu.platform.enrollment.domain.SagaTransition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaTransitionRepository extends JpaRepository<SagaTransition, Long> {
}