package com.dontaza.dontazabackend.auth.infrastructure;

import com.dontaza.dontazabackend.global.exception.BusinessViolationException.KakaoAuthFailedException;
import com.dontaza.dontazabackend.auth.infrastructure.dto.KakaoTokenResponse;
import com.dontaza.dontazabackend.auth.infrastructure.dto.KakaoUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class KakaoApiClient {

    private static final String UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    private final String restApiKey;
    private final String clientSecret;
    private final String adminKey;
    private final String tokenUrl;
    private final String userInfoUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public KakaoApiClient(
            @Value("${kakao.rest-api-key}") String restApiKey,
            @Value("${kakao.client-secret}") String clientSecret,
            @Value("${kakao.admin-key}") String adminKey,
            @Value("${kakao.token-url}") String tokenUrl,
            @Value("${kakao.user-info-url}") String userInfoUrl,
            ObjectMapper objectMapper
    ) {
        this.restApiKey = restApiKey;
        this.clientSecret = clientSecret;
        this.adminKey = adminKey;
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public KakaoTokenResponse requestToken(String authorizationCode, String redirectUri) {
        String body = "grant_type=authorization_code"
                + "&client_id=" + restApiKey
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&code=" + authorizationCode;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(5))
                .build();

        return sendRequest(request, KakaoTokenResponse.class);
    }

    public KakaoUserResponse requestUserInfo(String accessToken) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(userInfoUrl))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .GET()
                .timeout(Duration.ofSeconds(5))
                .build();

        return sendRequest(request, KakaoUserResponse.class);
    }

    public void unlinkUser(Long kakaoId) {
        String body = "target_id_type=user_id&target_id=" + kakaoId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(UNLINK_URL))
                .header("Authorization", "KakaoAK " + adminKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(5))
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new KakaoAuthFailedException();
        }
    }

    private <T> T sendRequest(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KakaoAuthFailedException();
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (KakaoAuthFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new KakaoAuthFailedException();
        }
    }
}
