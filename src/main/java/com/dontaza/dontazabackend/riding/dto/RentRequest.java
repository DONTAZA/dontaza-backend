package com.dontaza.dontazabackend.riding.dto;

public record RentRequest(
        String stationNo,
        Double lat,
        Double lng
) {
}
