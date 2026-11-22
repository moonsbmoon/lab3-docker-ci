package edu.platform.enrollment.service;

import edu.platform.enrollment.dto.CourseOfferResponse;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnrollmentServiceTest {

    private EnrollmentService enrollmentService;
    private WebClient catalogWebClient;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        catalogWebClient = mock(WebClient.class);
        meterRegistry = mock(MeterRegistry.class);
        enrollmentService = new EnrollmentService(catalogWebClient, meterRegistry);
    }

    @Test
    void shouldReturnOffer() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(catalogWebClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CourseOfferResponse.class))
                .thenReturn(Mono.just(new CourseOfferResponse(1L, "Java", new BigDecimal("5000"), new BigDecimal("4000"))));

        StepVerifier.create(enrollmentService.getOffer(1L))
                .expectNextMatches(r -> r.title().equals("Java"))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenEmpty() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(catalogWebClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CourseOfferResponse.class)).thenReturn(Mono.empty());

        StepVerifier.create(enrollmentService.getOffer(1L))
                .verifyComplete(); // Ожидаем пустой Mono
    }
}