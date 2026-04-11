package com.dontaza.dontazabackend.auth.dto;

public record TokenRefreshRequest(
        String refreshToken
) {
}
