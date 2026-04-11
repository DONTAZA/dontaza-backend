package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;

public record ReturnResponse(
        Long ridingId,
        int distanceMeters,
        int durationSeconds,
        int earnedPoints,
        String status
) {

    public static ReturnResponse from(Riding riding) {
        return new ReturnResponse(
                riding.getId(),
                riding.getDistanceMeters(),
                riding.getDurationSeconds(),
                riding.getEarnedPoints(),
                riding.getStatus().name()
        );
    }
}
