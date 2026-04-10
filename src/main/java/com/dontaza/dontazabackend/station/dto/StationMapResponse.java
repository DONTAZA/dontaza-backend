package com.dontaza.dontazabackend.station.dto;

import com.dontaza.dontazabackend.station.domain.Station;

import java.util.List;

public record StationMapResponse(List<StationItem> stations) {

    public record StationItem(
            String stationId,
            String name,
            double lat,
            double lng,
            int availableBikes
    ) {

        public static StationItem from(Station station) {
            return new StationItem(
                    station.id(),
                    station.name(),
                    station.lat(),
                    station.lng(),
                    station.availableBikes()
            );
        }
    }
}
