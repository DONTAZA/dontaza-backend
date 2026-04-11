package com.dontaza.dontazabackend.riding.dto;

public record ReturnRequest(
        String stationNo,
        Double lat,
        Double lng
) {
}
