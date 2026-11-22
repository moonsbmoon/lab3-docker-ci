package edu.platform.catalog;

import edu.platform.catalog.CourseRequest;
import edu.platform.catalog.CourseResponse;
import edu.platform.catalog.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Список курсов (STUDENT/TEACHER/ADMIN)")
    @GetMapping
    public List<CourseResponse> findAll() {
        return courseService.findAll();
    }

    @Operation(summary = "Курс по id")
    @GetMapping("/{id}")
    public CourseResponse findById(@PathVariable Long id) {
        return courseService.findById(id);
    }

    @Operation(summary = "Создание курса (TEACHER/ADMIN)")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.create(request, authentication.getName()));
    }

    @Operation(summary = "Обновление курса (TEACHER/ADMIN), пессимистическая блокировка")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable Long id,
                                 @Valid @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }
    @Operation(summary = "Список курсов вместе с уроками (для проверки N+1)")
    @GetMapping("/with-lessons")
    public List<CourseResponse> findAllWithLessons() {
        return courseService.findAllWithLessons();
    }
}