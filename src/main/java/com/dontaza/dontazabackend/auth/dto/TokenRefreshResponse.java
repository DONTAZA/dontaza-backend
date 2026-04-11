package com.dontaza.dontazabackend.auth.dto;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
}
