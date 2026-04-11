package com.dontaza.dontazabackend.station.api;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.station.dto.StationVerifyRequest;
import com.dontaza.dontazabackend.station.dto.StationVerifyResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface StationApi {

    @PostMapping("/verify")
    SuccessResponse<StationVerifyResponse> verifyProximity(@RequestBody StationVerifyRequest request);
}
