package com.dontaza.dontazabackend.station.application;

import com.dontaza.dontazabackend.global.exception.BusinessViolationException.TooFarFromStationException;
import com.dontaza.dontazabackend.global.exception.ResourceException.StationNotFoundException;
import com.dontaza.dontazabackend.station.domain.GeoPoint;
import com.dontaza.dontazabackend.station.domain.Station;
import com.dontaza.dontazabackend.station.domain.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private static final int PROXIMITY_RADIUS_METERS = 50;

    private final StationRepository stationRepository;

    public List<Station> findNearbyStations(double lat, double lng) {
        GeoPoint userLocation = new GeoPoint(lat, lng);
        return stationRepository.findAll().stream()
                .filter(s -> s.isWithinRadius(userLocation, PROXIMITY_RADIUS_METERS))
                .toList();
    }

    public Station findNearestStation(double lat, double lng) {
        GeoPoint userLocation = new GeoPoint(lat, lng);
        return stationRepository.findAll().stream()
                .filter(s -> s.isWithinRadius(userLocation, PROXIMITY_RADIUS_METERS))
                .min(Comparator.comparingInt(s -> s.distanceMetersTo(userLocation)))
                .orElseThrow(TooFarFromStationException::new);
    }

    public Station findById(String stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);
    }
}
