package edu.platform.enrollment.client;

import edu.platform.enrollment.dto.CourseOfferResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("/api/courses")
public interface CatalogClient {

    @GetExchange("/{id}/offer")
    Mono<CourseOfferResponse> getCourseOffer(
            @PathVariable Long id,
            @RequestHeader("API-Version") String apiVersion
    );
}