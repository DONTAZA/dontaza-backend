package com.dontaza.dontazabackend.station.controller;

import com.dontaza.dontazabackend.global.response.ApiResponse;
import com.dontaza.dontazabackend.station.api.StationApi;
import com.dontaza.dontazabackend.station.application.StationService;
import com.dontaza.dontazabackend.station.domain.GeoPoint;
import com.dontaza.dontazabackend.station.domain.MapBounds;
import com.dontaza.dontazabackend.station.dto.StationMapResponse;
import com.dontaza.dontazabackend.station.dto.StationNearbyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController implements StationApi {

    private final StationService stationService;

    @Override
    public ApiResponse<StationMapResponse> getStationsInBounds(
            double swLat, double swLng, double neLat, double neLng) {
        MapBounds bounds = new MapBounds(new GeoPoint(swLat, swLng), new GeoPoint(neLat, neLng));
        return ApiResponse.success(stationService.findStationsInBounds(bounds));
    }

    @Override
    public ApiResponse<StationNearbyResponse> getNearbyStations(
            double lat, double lng, int radius) {
        GeoPoint center = new GeoPoint(lat, lng);
        return ApiResponse.success(stationService.findNearbyStations(center, radius));
    }
}
