package edu.platform.catalog.dto;

import java.math.BigDecimal;

public record CourseOfferResponse(
        Long courseId,
        String title,
        BigDecimal price,
        BigDecimal premiumPrice
) {}