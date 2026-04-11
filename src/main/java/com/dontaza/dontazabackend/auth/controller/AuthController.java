package com.dontaza.dontazabackend.auth.controller;

import com.dontaza.dontazabackend.auth.api.AuthApi;
import com.dontaza.dontazabackend.auth.application.AuthService;
import com.dontaza.dontazabackend.auth.dto.KakaoLoginRequest;
import com.dontaza.dontazabackend.auth.dto.LoginResponse;
import com.dontaza.dontazabackend.auth.dto.TokenRefreshRequest;
import com.dontaza.dontazabackend.auth.dto.TokenRefreshResponse;
import com.dontaza.dontazabackend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public SuccessResponse<LoginResponse> kakaoLogin(KakaoLoginRequest request) {
        return SuccessResponse.success(HttpStatus.OK, authService.kakaoLogin(request));
    }

    @Override
    public SuccessResponse<TokenRefreshResponse> refreshToken(TokenRefreshRequest request) {
        return SuccessResponse.success(HttpStatus.OK, authService.refreshToken(request.refreshToken()));
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public SuccessResponse<Void> logout() {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.logout(memberId);
        return SuccessResponse.success(HttpStatus.NO_CONTENT);
    }
}
