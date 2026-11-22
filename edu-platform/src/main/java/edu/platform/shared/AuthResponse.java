package edu.platform.shared;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String email,
        String role
) {}