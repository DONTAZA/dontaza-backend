package com.dontaza.dontazabackend.station.dto;

import com.dontaza.dontazabackend.station.domain.Station;

import java.util.List;

public record StationNearbyResponse(
        List<StationItem> stations,
        Bounds bounds,
        String updatedAt
) {

    public record StationItem(
            String stationId,
            String name,
            double lat,
            double lng,
            int distanceMeters,
            int availableBikes
    ) {

        public static StationItem from(Station station, int distanceMeters) {
            return new StationItem(
                    station.id(),
                    station.name(),
                    station.lat(),
                    station.lng(),
                    distanceMeters,
                    station.availableBikes()
            );
        }
    }

    public record Bounds(LatLng sw, LatLng ne) {
    }

    public record LatLng(double lat, double lng) {
    }
}
