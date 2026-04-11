package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;

public record RentResponse(
        Long ridingId,
        String stationNo,
        String stationName,
        String status
) {

    public static RentResponse from(Riding riding) {
        return new RentResponse(
                riding.getId(),
                riding.getRentStationNo(),
                riding.getRentStationName(),
                riding.getStatus().name()
        );
    }
}
