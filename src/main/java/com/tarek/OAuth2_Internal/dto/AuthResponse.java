package com.tarek.OAuth2_Internal.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String name,
        String email,
        String role,
        boolean emailVerified
) {
}
