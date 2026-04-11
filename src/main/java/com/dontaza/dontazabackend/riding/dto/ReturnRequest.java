package com.dontaza.dontazabackend.riding.dto;

public record ReturnRequest(
        Double lat,
        Double lng,
        int earnedPoints
) {
}
