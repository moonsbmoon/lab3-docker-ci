package edu.platform.enrollment.web;

import edu.platform.enrollment.service.EnrollmentCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/enrollments") // <--- Вот правильный базовый путь
@RequiredArgsConstructor
public class EnrollmentCreationController {

    private final EnrollmentCreationService enrollmentCreationService;

    @PostMapping
    public Mono<String> createEnrollment(
            @RequestParam Long courseId,
            @RequestParam Long userId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        return Mono.fromCallable(() ->
                enrollmentCreationService.createEnrollment(courseId, userId, idempotencyKey)
        );
    }
}