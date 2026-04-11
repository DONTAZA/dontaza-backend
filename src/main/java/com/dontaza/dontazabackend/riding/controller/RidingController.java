package com.dontaza.dontazabackend.riding.controller;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.riding.api.RidingApi;
import com.dontaza.dontazabackend.riding.application.RidingService;
import com.dontaza.dontazabackend.riding.dto.RentRequest;
import com.dontaza.dontazabackend.riding.dto.RentResponse;
import com.dontaza.dontazabackend.riding.dto.ReturnRequest;
import com.dontaza.dontazabackend.riding.dto.ReturnResponse;
import com.dontaza.dontazabackend.riding.dto.RidingCurrentResponse;
import com.dontaza.dontazabackend.riding.dto.VerifyResponse;
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

    @Override
    public SuccessResponse<RidingCurrentResponse> getCurrentRiding() {
        // TODO: 인증 구현 후 JWT에서 userId 추출로 교체
        Long userId = 1L;
        return SuccessResponse.success(HttpStatus.OK, ridingService.getCurrentRiding(userId));
    }

    @Override
    public SuccessResponse<VerifyResponse> verify(Long ridingId) {
        return SuccessResponse.success(HttpStatus.OK, ridingService.verify(ridingId));
    }

    @Override
    public SuccessResponse<ReturnResponse> returnBike(Long ridingId, ReturnRequest request) {
        // TODO: 인증 구현 후 JWT에서 userId 추출로 교체
        Long userId = 1L;
        return SuccessResponse.success(HttpStatus.OK, ridingService.returnBike(userId, ridingId, request));
    }
}
