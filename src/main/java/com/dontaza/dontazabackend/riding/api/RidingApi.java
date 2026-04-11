package com.dontaza.dontazabackend.riding.api;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.riding.dto.RentRequest;
import com.dontaza.dontazabackend.riding.dto.RentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface RidingApi {

    @PostMapping("/rent")
    SuccessResponse<RentResponse> rent(@RequestBody RentRequest request);
}
