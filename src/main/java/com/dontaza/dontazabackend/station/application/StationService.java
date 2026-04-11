package com.dontaza.dontazabackend.station.application;

import com.dontaza.dontazabackend.global.exception.BusinessViolationException.TooFarFromStationException;
import com.dontaza.dontazabackend.global.exception.ResourceException.StationNotFoundException;
import com.dontaza.dontazabackend.station.domain.GeoPoint;
import com.dontaza.dontazabackend.station.domain.Station;
import com.dontaza.dontazabackend.station.dto.StationVerifyRequest;
import com.dontaza.dontazabackend.station.dto.StationVerifyResponse;
import com.dontaza.dontazabackend.station.infrastructure.PublicBikeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private static final int PROXIMITY_RADIUS_METERS = 50;

    private final PublicBikeApiClient publicBikeApiClient;

    public StationVerifyResponse verifyProximity(StationVerifyRequest request) {
        Station station = findByStationNo(request.stationNo());
        GeoPoint userLocation = new GeoPoint(request.lat(), request.lng());
        int distance = station.distanceMetersTo(userLocation);
        boolean withinRange = distance <= PROXIMITY_RADIUS_METERS;

        return new StationVerifyResponse(
                station.id(),
                station.name(),
                station.lat(),
                station.lng(),
                withinRange,
                distance
        );
    }

    public Station findByStationNo(String stationNo) {
        List<Station> stations = publicBikeApiClient.fetchAllStations();
        return stations.stream()
                .filter(station -> station.name().startsWith(stationNo + "."))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    public void validateProximity(String stationNo, double lat, double lng) {
        Station station = findByStationNo(stationNo);
        GeoPoint userLocation = new GeoPoint(lat, lng);
        if (station.distanceMetersTo(userLocation) > PROXIMITY_RADIUS_METERS) {
            throw new TooFarFromStationException();
        }
    }
}
