package com.dontaza.dontazabackend.riding.controller;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.riding.api.RidingApi;
import com.dontaza.dontazabackend.riding.application.RidingService;
import com.dontaza.dontazabackend.riding.dto.RentRequest;
import com.dontaza.dontazabackend.riding.dto.RentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/riding")
@RequiredArgsConstructor
public class RidingController implements RidingApi {

    private final RidingService ridingService;

    @Override
    public SuccessResponse<RentResponse> rent(RentRequest request) {
        // TODO: 인증 구현 후 JWT에서 userId 추출로 교체
        Long userId = 1L;
        return SuccessResponse.success(HttpStatus.OK, ridingService.rent(userId, request));
    }
}
