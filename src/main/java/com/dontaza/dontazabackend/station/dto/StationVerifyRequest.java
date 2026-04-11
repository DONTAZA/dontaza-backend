package com.dontaza.dontazabackend.station.dto;

public record StationVerifyRequest(
        String stationNo,
        Double lat,
        Double lng
) {
}
