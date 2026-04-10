package com.dontaza.dontazabackend.station.api;

import com.dontaza.dontazabackend.global.response.ApiResponse;
import com.dontaza.dontazabackend.station.dto.StationMapResponse;
import com.dontaza.dontazabackend.station.dto.StationNearbyResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface StationApi {

    @GetMapping
    ApiResponse<StationMapResponse> getStationsInBounds(
            @RequestParam double swLat,
            @RequestParam double swLng,
            @RequestParam double neLat,
            @RequestParam double neLng);

    @GetMapping("/nearby")
    ApiResponse<StationNearbyResponse> getNearbyStations(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "500") int radius);
}
