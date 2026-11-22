package edu.platform.enrollment.web;

import edu.platform.enrollment.service.SagaOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class PaidEnrollmentController {

    private final SagaOrchestratorService sagaOrchestratorService;

    @PostMapping("/paid")
    public Mono<String> createPaidEnrollment(
            @RequestParam Long courseId,
            @RequestParam Long userId) {

        return Mono.fromCallable(() -> sagaOrchestratorService.startPaidEnrollment(courseId, userId))
                .subscribeOn(Schedulers.boundedElastic());
    }
}