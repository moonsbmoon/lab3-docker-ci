package edu.platform.catalog;

import jakarta.persistence.LockModeType; // ДОБАВЛЕН ИМПОРТ
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    // Для устранения N+1
    @EntityGraph(attributePaths = {"lessons"})
    @Query("SELECT c FROM Course c")
    List<Course> findAllWithLessons();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Course c where c.id = :id")
    Optional<Course> findByIdForUpdate(@Param("id") Long id);

    @Query(value = """
        SELECT c.id as courseId, c.title as title, 
               COUNT(CASE WHEN e.status = 'ACTIVE' THEN 1 END) as activeCount,
               COUNT(CASE WHEN e.status = 'ACTIVE' AND s.tariff = 'PREMIUM' THEN 1 END) as premiumCount
        FROM courses c
        LEFT JOIN enrollments e ON c.id = e.course_id
        LEFT JOIN users u ON e.user_id = u.id
        LEFT JOIN subscriptions s ON u.id = s.user_id
        GROUP BY c.id, c.title
        """, nativeQuery = true)
    List<CourseReportDto> getCourseReport();
}