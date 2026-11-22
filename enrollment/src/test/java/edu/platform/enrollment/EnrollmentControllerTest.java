package edu.platform.enrollment;

import edu.platform.enrollment.dto.CourseOfferResponse;
import edu.platform.enrollment.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = edu.platform.enrollment.web.EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EnrollmentService enrollmentService;

    @Test
    void shouldReturnOfferSuccessfully() {
        CourseOfferResponse mockResponse = new CourseOfferResponse(1L, "Java", new BigDecimal("5000"), new BigDecimal("4000"));
        when(enrollmentService.getOffer(1L)).thenReturn(Mono.just(mockResponse));

        webTestClient.get().uri("/api/enrollment-offers?courseId=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Java");
    }

    @Test
    void shouldReturnNotFound() {
        when(enrollmentService.getOffer(999L))
                .thenReturn(Mono.error(new EnrollmentService.CourseNotFoundException("Not found")));

        webTestClient.get().uri("/api/enrollment-offers?courseId=999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("COURSE_NOT_FOUND");
    }

    @Test
    void shouldVerifyReactiveChainWithStepVerifier() {
        CourseOfferResponse mockResponse = new CourseOfferResponse(1L, "Java", new BigDecimal("5000"), new BigDecimal("4000"));
        when(enrollmentService.getOffer(1L)).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(enrollmentService.getOffer(1L))
                .expectNextMatches(res -> res.title().equals("Java"))
                .verifyComplete();

        // Тест на пустой результат
        StepVerifier.create(Mono.empty())
                .verifyComplete();

        // Тест на ошибку
        StepVerifier.create(Mono.error(new RuntimeException("Test error")))
                .expectError(RuntimeException.class)
                .verify();
    }
}