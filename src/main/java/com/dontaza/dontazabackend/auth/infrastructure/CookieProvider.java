package com.dontaza.dontazabackend.auth.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieProvider {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public CookieProvider(
            final @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
            final @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry
    ) {
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public ResponseCookie createAccessTokenCookie(final String token) {
        return buildCookie(ACCESS_TOKEN_COOKIE, token, accessTokenExpiry / 1000, "/");
    }

    public ResponseCookie createRefreshTokenCookie(final String token) {
        return buildCookie(REFRESH_TOKEN_COOKIE, token, refreshTokenExpiry / 1000, "/api/auth");
    }

    public ResponseCookie deleteAccessTokenCookie() {
        return buildCookie(ACCESS_TOKEN_COOKIE, "", 0, "/");
    }

    public ResponseCookie deleteRefreshTokenCookie() {
        return buildCookie(REFRESH_TOKEN_COOKIE, "", 0, "/api/auth");
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private ResponseCookie buildCookie(final String name, final String value, final long maxAgeSeconds, final String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path(path)
                .maxAge(maxAgeSeconds)
                .sameSite("None")
                .build();
    }
}
