package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;

import java.time.Duration;
import java.time.LocalDateTime;

public record RidingCurrentResponse(
        Long ridingId,
        String stationNo,
        String stationName,
        String status,
        long elapsedSeconds,
        LocalDateTime rentedAt
) {

    public static RidingCurrentResponse from(Riding riding) {
        long elapsed = Duration.between(riding.getRentedAt(), LocalDateTime.now()).getSeconds();
        return new RidingCurrentResponse(
                riding.getId(),
                riding.getRentStationNo(),
                riding.getRentStationName(),
                riding.getStatus().name(),
                elapsed,
                riding.getRentedAt()
        );
    }
}
