package com.dontaza.dontazabackend.auth.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtProvider(
            final @Value("${jwt.secret-key}") String secretKey,
            final @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
            final @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String createAccessToken(final Long memberId, final String role) {
        return createToken(memberId, role, accessTokenExpiry);
    }

    public String createRefreshToken(final Long memberId) {
        return createToken(memberId, null, refreshTokenExpiry);
    }

    public Long getMemberId(final String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    public String getRole(final String token) {
        return parseClaims(token).get("role", String.class);
    }

    public boolean isValid(final String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    private String createToken(final Long memberId, final String role, final long expiry) {
        Date now = new Date();
        var builder = Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiry))
                .signWith(secretKey);
        if (role != null) {
            builder.claim("role", role);
        }
        return builder.compact();
    }

    private Claims parseClaims(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
