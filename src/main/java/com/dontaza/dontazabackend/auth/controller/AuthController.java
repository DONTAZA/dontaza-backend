package com.dontaza.dontazabackend.auth.controller;

import com.dontaza.dontazabackend.auth.api.AuthApi;
import com.dontaza.dontazabackend.auth.application.AuthService;
import com.dontaza.dontazabackend.auth.dto.KakaoLoginRequest;
import com.dontaza.dontazabackend.auth.dto.LoginResponse;
import com.dontaza.dontazabackend.auth.dto.LoginResult;
import com.dontaza.dontazabackend.auth.dto.TokenResult;
import com.dontaza.dontazabackend.auth.infrastructure.CookieProvider;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.InvalidRefreshTokenException;
import com.dontaza.dontazabackend.global.response.SuccessResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    @Override
    public SuccessResponse<LoginResponse> kakaoLogin(KakaoLoginRequest request,
                                                      HttpServletResponse response) {
        LoginResult result = authService.kakaoLogin(request);
        addTokenCookies(response, result.accessToken(), result.refreshToken());
        return SuccessResponse.success(HttpStatus.OK,
                LoginResponse.from(result.isNewUser(), result.member()));
    }

    @Override
    public SuccessResponse<Void> refreshToken(HttpServletRequest request,
                                               HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        TokenResult result = authService.refreshToken(refreshToken);
        addTokenCookies(response, result.accessToken(), result.refreshToken());
        return SuccessResponse.success(HttpStatus.OK);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Void> logout(HttpServletResponse response) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.logout(memberId);
        deleteTokenCookies(response);
        return SuccessResponse.success(HttpStatus.NO_CONTENT);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Void> withdraw(HttpServletResponse response) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.withdraw(memberId);
        deleteTokenCookies(response);
        return SuccessResponse.success(HttpStatus.NO_CONTENT);
    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieProvider.createAccessTokenCookie(accessToken).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieProvider.createRefreshTokenCookie(refreshToken).toString());
    }

    private void deleteTokenCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieProvider.deleteAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieProvider.deleteRefreshTokenCookie().toString());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new InvalidRefreshTokenException();
    }
}
