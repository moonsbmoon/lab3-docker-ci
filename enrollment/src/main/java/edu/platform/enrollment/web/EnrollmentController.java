package edu.platform.enrollment.web;

import edu.platform.enrollment.dto.CourseOfferResponse;
import edu.platform.enrollment.service.EnrollmentService;
import edu.platform.enrollment.service.TestRabbitProducer;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/enrollment-offers")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final TestRabbitProducer testRabbitProducer;

    public EnrollmentController(EnrollmentService enrollmentService, TestRabbitProducer testRabbitProducer) {
        this.enrollmentService = enrollmentService;
        this.testRabbitProducer = testRabbitProducer;
    }

    @GetMapping
    public Mono<CourseOfferResponse> getOffer(@RequestParam Long courseId) {
        return enrollmentService.getOffer(courseId);
    }

    @PostMapping("/test-rabbit")
    public Mono<String> testRabbit() {
        return Mono.fromRunnable(() -> testRabbitProducer.sendTestEvent(1L, 100L, 50L))
                .thenReturn("Тестовое сообщение успешно отправлено в RabbitMQ!");
    }
}