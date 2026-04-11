package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;

import java.time.LocalDateTime;

public record RidingCurrentResponse(
        Long ridingId,
        String stationNo,
        String stationName,
        String status,
        LocalDateTime rentedAt
) {

    public static RidingCurrentResponse from(Riding riding) {
        return new RidingCurrentResponse(
                riding.getId(),
                riding.getRentStationNo(),
                riding.getRentStationName(),
                riding.getStatus().name(),
                riding.getRentedAt()
        );
    }
}
