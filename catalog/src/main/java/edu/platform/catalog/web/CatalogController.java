package edu.platform.catalog.web;

import edu.platform.catalog.config.CatalogProperties;
import edu.platform.catalog.dto.CourseOfferResponse;
import edu.platform.pricing.PriceCalculator;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CatalogController {

    private final Map<Long, String> courses = Map.of(
            1L, "Java Spring Boot",
            2L, "Python для начинающих"
    );

    private final PriceCalculator priceCalculator;
    private final MeterRegistry meterRegistry;
    private final CatalogProperties properties;

    public CatalogController(PriceCalculator priceCalculator, MeterRegistry meterRegistry, CatalogProperties properties) {
        this.priceCalculator = priceCalculator;
        this.meterRegistry = meterRegistry;
        this.properties = properties;
    }

    @GetMapping("/{id}/offer")
    public Mono<CourseOfferResponse> getCourseOffer(@PathVariable Long id) {
        return Mono.defer(() -> {
            String title = courses.get(id);
            if (title == null) {
                meterRegistry.counter("catalog_offer_requests_total", "operation", "get_offer", "result", "not_found").increment();
                return Mono.error(new CourseNotFoundException("Курс с ID " + id + " не найден"));
            }

            BigDecimal basePrice = new BigDecimal("5000");

            // Исправление: конвертируем BigDecimal в double для вызова метода библиотеки
            double basePriceDouble = basePrice.doubleValue();
            double premiumPriceDouble = priceCalculator.calculatePremiumPrice(basePriceDouble);
            // Конвертируем обратно в BigDecimal
            BigDecimal premiumPrice = BigDecimal.valueOf(premiumPriceDouble);

            meterRegistry.counter("catalog_offer_requests_total", "operation", "get_offer", "result", "success").increment();
            meterRegistry.counter("catalog_variant_premium_requests_total").increment();

            return Mono.just(new CourseOfferResponse(id, title, basePrice, premiumPrice));
        }).delayElement(Duration.ofMillis(properties.delayMillis()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class CourseNotFoundException extends RuntimeException {
        public CourseNotFoundException(String message) { super(message); }
    }
}