package com.dontaza.dontazabackend.auth.dto;

public record KakaoLoginRequest(
        String authorizationCode,
        String redirectUri
) {
}
