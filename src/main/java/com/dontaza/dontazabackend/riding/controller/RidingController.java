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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/riding")
@RequiredArgsConstructor
public class RidingController implements RidingApi {

    private final RidingService ridingService;

    @Override
    public SuccessResponse<RentResponse> rent(RentRequest request) {
        return SuccessResponse.success(HttpStatus.OK, ridingService.rent(getCurrentMemberId(), request));
    }

    @Override
    public SuccessResponse<RidingCurrentResponse> getCurrentRiding() {
        return SuccessResponse.success(HttpStatus.OK, ridingService.getCurrentRiding(getCurrentMemberId()));
    }

    @Override
    public SuccessResponse<VerifyResponse> verify() {
        return SuccessResponse.success(HttpStatus.OK, ridingService.verify(getCurrentMemberId()));
    }

    @Override
    public SuccessResponse<ReturnResponse> returnBike(ReturnRequest request) {
        return SuccessResponse.success(HttpStatus.OK, ridingService.returnBike(getCurrentMemberId(), request));
    }

    private Long getCurrentMemberId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
