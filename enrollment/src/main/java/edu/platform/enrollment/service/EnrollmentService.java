package edu.platform.enrollment.service;

import edu.platform.enrollment.dto.CourseOfferResponse;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class EnrollmentService {

    private final WebClient catalogWebClient;
    private final MeterRegistry meterRegistry;

    public EnrollmentService(WebClient catalogWebClient, MeterRegistry meterRegistry) {
        this.catalogWebClient = catalogWebClient;
        this.meterRegistry = meterRegistry;
    }

    public Mono<CourseOfferResponse> getOffer(Long courseId) {
        return catalogWebClient.get()
                .uri("/api/courses/{id}/offer", courseId)
                .retrieve()
                .bodyToMono(CourseOfferResponse.class)
                .doOnSuccess(r -> meterRegistry.counter("enrollment_offer_requests_total", "operation", "get_offer", "result", "success").increment())
                .doOnError(e -> meterRegistry.counter("enrollment_offer_requests_total", "operation", "get_offer", "result", "error").increment())
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    return Mono.error(new CourseNotFoundException("Курс с ID " + courseId + " не найден в каталоге"));
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().is5xxServerError()) {
                        return Mono.error(new CatalogUnavailableException("Сервис каталога вернул ошибку: " + ex.getStatusCode()));
                    }
                    return Mono.error(ex);
                })
                .onErrorResume(WebClientRequestException.class, ex -> {
                    return Mono.error(new CatalogUnavailableException("Сервис каталога временно недоступен"));
                });
    }

    public static class CourseNotFoundException extends RuntimeException {
        public CourseNotFoundException(String message) { super(message); }
    }

    public static class CatalogUnavailableException extends RuntimeException {
        public CatalogUnavailableException(String message) { super(message); }
    }
}