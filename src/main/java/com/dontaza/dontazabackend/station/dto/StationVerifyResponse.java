package com.dontaza.dontazabackend.station.dto;

public record StationVerifyResponse(
        String stationNo,
        String stationName,
        boolean withinRange,
        int distanceMeters
) {
}
