package edu.platform.catalog;

import edu.platform.lessons.Lesson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String summary;
    private BigDecimal price;
    private Long teacherId;

    @Version
    private Long version; // <-- ИСПРАВЛЕНИЕ: Изменено с Integer на Long

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private List<Lesson> lessons;
}