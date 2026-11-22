package edu.platform.subscriptions;

import java.time.Instant;

public record SubscriptionResponse(
        Long id,
        String tariff, // <-- Заменили plan на tariff
        Instant startedAt,
        Instant expiresAt
) {}