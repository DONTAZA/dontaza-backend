package com.dontaza.dontazabackend.member.api;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.member.dto.PointResponse;
import org.springframework.web.bind.annotation.GetMapping;

public interface PointApi {

    @GetMapping("/me")
    SuccessResponse<PointResponse> getMyPoints();
}
