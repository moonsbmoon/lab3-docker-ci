package edu.platform.enrollment.repository;

import edu.platform.enrollment.domain.Enrollment; // <--- Убедитесь, что этот импорт есть!
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}