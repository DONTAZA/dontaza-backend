package com.dontaza.dontazabackend.station.controller;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.station.api.StationApi;
import com.dontaza.dontazabackend.station.application.StationService;
import com.dontaza.dontazabackend.station.dto.StationVerifyRequest;
import com.dontaza.dontazabackend.station.dto.StationVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController implements StationApi {

    private final StationService stationService;

    @Override
    public SuccessResponse<StationVerifyResponse> verifyProximity(StationVerifyRequest request) {
        return SuccessResponse.success(HttpStatus.OK, stationService.verifyProximity(request));
    }
}
