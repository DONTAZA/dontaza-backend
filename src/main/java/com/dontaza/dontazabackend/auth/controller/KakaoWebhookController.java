package com.dontaza.dontazabackend.auth.controller;

import com.dontaza.dontazabackend.auth.application.AuthService;
import com.dontaza.dontazabackend.auth.dto.KakaoUnlinkRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoWebhookController {

    private final AuthService authService;

    @PostMapping("/unlink")
    public ResponseEntity<Void> handleUnlink(@RequestBody KakaoUnlinkRequest request) {
        log.info("Kakao unlink webhook received: kakaoId={}", request.userId());
        authService.withdrawByKakaoId(request.userId());
        return ResponseEntity.ok().build();
    }
}
