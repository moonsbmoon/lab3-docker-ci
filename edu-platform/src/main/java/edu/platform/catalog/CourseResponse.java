package edu.platform.catalog;

import edu.platform.lessons.LessonResponse;
import java.math.BigDecimal;
import java.util.List;

public record CourseResponse(
        Long id,
        String title,
        String summary,
        BigDecimal price,
        Long teacherId,
        Long version,
        List<LessonResponse> lessons // <-- Добавлено для проверки N+1
) {}