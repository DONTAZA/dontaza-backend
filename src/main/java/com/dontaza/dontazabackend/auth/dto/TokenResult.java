package com.dontaza.dontazabackend.auth.dto;

public record TokenResult(
        String accessToken,
        String refreshToken
) {
}
