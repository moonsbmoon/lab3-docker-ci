package edu.platform.enrollment.dto;

import java.math.BigDecimal;

public record EnrollmentResponseV2(
        Long enrollmentId,
        String status,
        String courseTitle,
        BigDecimal price,
        String subscriptionOffer, // "PREMIUM"
        boolean subscriptionAvailable // true
) {}