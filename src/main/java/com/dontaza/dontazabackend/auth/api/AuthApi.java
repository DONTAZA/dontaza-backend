package com.dontaza.dontazabackend.auth.api;

import com.dontaza.dontazabackend.auth.dto.KakaoLoginRequest;
import com.dontaza.dontazabackend.auth.dto.LoginResponse;
import com.dontaza.dontazabackend.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthApi {

    @PostMapping("/kakao")
    SuccessResponse<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request,
                                               HttpServletResponse response);

    @PostMapping("/token/refresh")
    SuccessResponse<Void> refreshToken(HttpServletRequest request,
                                       HttpServletResponse response);

    @DeleteMapping("/logout")
    SuccessResponse<Void> logout(HttpServletResponse response);

    @PatchMapping("/agree")
    SuccessResponse<Void> agreeToTerms();

    @DeleteMapping("/withdraw")
    SuccessResponse<Void> withdraw(HttpServletResponse response);
}
