package com.dontaza.dontazabackend.station.application;

import com.dontaza.dontazabackend.station.domain.GeoPoint;
import com.dontaza.dontazabackend.station.domain.MapBounds;
import com.dontaza.dontazabackend.station.domain.Station;
import com.dontaza.dontazabackend.station.dto.StationMapResponse;
import com.dontaza.dontazabackend.station.dto.StationNearbyResponse;
import com.dontaza.dontazabackend.station.dto.StationNearbyResponse.Bounds;
import com.dontaza.dontazabackend.station.dto.StationNearbyResponse.LatLng;
import com.dontaza.dontazabackend.station.dto.StationNearbyResponse.StationItem;
import com.dontaza.dontazabackend.station.infrastructure.PublicBikeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final PublicBikeApiClient publicBikeApiClient;

    public StationMapResponse findStationsInBounds(MapBounds bounds) {
        List<Station> allStations = publicBikeApiClient.fetchAllStations();
        List<StationMapResponse.StationItem> items = allStations.stream()
                .filter(station -> station.isWithinBounds(bounds))
                .map(StationMapResponse.StationItem::from)
                .toList();
        return new StationMapResponse(items);
    }

    public StationNearbyResponse findNearbyStations(GeoPoint center, int radius) {
        List<Station> allStations = publicBikeApiClient.fetchAllStations();
        List<Station> nearbyStations = filterByRadius(allStations, center, radius);
        return buildResponse(nearbyStations, center);
    }

    private List<Station> filterByRadius(List<Station> stations, GeoPoint center, int radius) {
        return stations.stream()
                .filter(station -> station.isWithinRadius(center, radius))
                .sorted(Comparator.comparingInt(station -> station.distanceMetersTo(center)))
                .toList();
    }

    private StationNearbyResponse buildResponse(List<Station> stations, GeoPoint center) {
        List<StationItem> items = toStationItems(stations, center);
        Bounds bounds = calculateBounds(stations);
        return new StationNearbyResponse(items, bounds, Instant.now().toString());
    }

    private List<StationItem> toStationItems(List<Station> stations, GeoPoint center) {
        return stations.stream()
                .map(station -> StationItem.from(station, station.distanceMetersTo(center)))
                .toList();
    }

    private Bounds calculateBounds(List<Station> stations) {
        if (stations.isEmpty()) {
            return null;
        }
        DoubleSummaryStatistics latStats = stations.stream()
                .mapToDouble(Station::lat).summaryStatistics();
        DoubleSummaryStatistics lngStats = stations.stream()
                .mapToDouble(Station::lng).summaryStatistics();
        return new Bounds(
                new LatLng(latStats.getMin(), lngStats.getMin()),
                new LatLng(latStats.getMax(), lngStats.getMax())
        );
    }
}
