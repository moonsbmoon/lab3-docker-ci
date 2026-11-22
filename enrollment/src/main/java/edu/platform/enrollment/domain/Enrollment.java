package edu.platform.enrollment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courseId;
    private Long userId;
    private Instant createdAt = Instant.now();

    public Enrollment(Long courseId, Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }
}