package com.project.checkinn.security;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {}