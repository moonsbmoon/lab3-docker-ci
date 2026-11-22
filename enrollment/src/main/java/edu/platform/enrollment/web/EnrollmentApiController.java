package edu.platform.enrollment.web;

import edu.platform.enrollment.client.CatalogClient;
import edu.platform.enrollment.domain.Enrollment;
import edu.platform.enrollment.dto.EnrollmentResponseV1;
import edu.platform.enrollment.dto.EnrollmentResponseV2;
import edu.platform.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentApiController {

    private final EnrollmentRepository enrollmentRepository;
    private final CatalogClient catalogClient;

    @GetMapping("/{id}")
    public Mono<?> getEnrollment(
            @PathVariable Long id,
            @RequestHeader(value = "API-Version", required = false) String apiVersion) {

        // 1. Проверка версии (Задание 13)
        if (apiVersion == null || (!apiVersion.equals("1.0") && !apiVersion.equals("2.0"))) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid API-Version. Use 1.0 or 2.0"));
        }

        // 2. Находим запись в БД (блокирующий вызов выносим в boundedElastic)
        return Mono.fromCallable(() -> enrollmentRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found")))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(enrollment -> {

                    // 3. Версия 1.0 — старый формат (без обращения в catalog)
                    if (apiVersion.equals("1.0")) {
                        return Mono.just(new EnrollmentResponseV1(enrollment.getId(), "ACTIVE"));
                    }

                    // 4. Версия 2.0 — идем в catalog (с таймаутом 1 секунда)
                    return catalogClient.getCourseOffer(enrollment.getCourseId(), "2.0")
                            .timeout(Duration.ofSeconds(1))
                            .map(course -> new EnrollmentResponseV2(
                                    enrollment.getId(),
                                    "ACTIVE",
                                    course.title(),
                                    course.price(),
                                    "PREMIUM",   // Данные варианта 3
                                    true          // subscriptionAvailable
                            ))
                            .onErrorResume(e -> Mono.error(new ResponseStatusException(
                                    HttpStatus.SERVICE_UNAVAILABLE, "Catalog is unavailable")));
                });
    }
}