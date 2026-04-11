package com.dontaza.dontazabackend.auth.api;

import com.dontaza.dontazabackend.auth.dto.KakaoLoginRequest;
import com.dontaza.dontazabackend.auth.dto.LoginResponse;
import com.dontaza.dontazabackend.auth.dto.TokenRefreshRequest;
import com.dontaza.dontazabackend.auth.dto.TokenRefreshResponse;
import com.dontaza.dontazabackend.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthApi {

    @PostMapping("/kakao")
    SuccessResponse<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request);

    @PostMapping("/token/refresh")
    SuccessResponse<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request);

    @DeleteMapping("/logout")
    SuccessResponse<Void> logout();
}
