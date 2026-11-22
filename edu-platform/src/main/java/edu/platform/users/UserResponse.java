package edu.platform.users;

import edu.platform.subscriptions.SubscriptionResponse;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        String role,
        SubscriptionResponse subscription
) {}