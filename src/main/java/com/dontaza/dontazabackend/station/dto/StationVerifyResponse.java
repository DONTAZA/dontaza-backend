package com.dontaza.dontazabackend.station.dto;

public record StationVerifyResponse(
        String stationNo,
        String stationName,
        double stationLat,
        double stationLng,
        boolean withinRange,
        int distanceMeters
) {
}
