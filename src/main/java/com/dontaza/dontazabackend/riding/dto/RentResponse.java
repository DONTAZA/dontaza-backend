package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;
import com.dontaza.dontazabackend.station.domain.Station;

import java.util.List;

public record RentResponse(
        Long ridingId,
        List<String> nearbyStationIds,
        String status
) {

    public static RentResponse of(Riding riding, List<Station> nearbyStations) {
        List<String> stationIds = nearbyStations.stream()
                .map(Station::getId)
                .toList();
        return new RentResponse(
                riding.getId(),
                stationIds,
                riding.getStatus().name()
        );
    }
}
