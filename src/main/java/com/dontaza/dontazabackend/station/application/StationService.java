package com.dontaza.dontazabackend.station.application;

import com.dontaza.dontazabackend.global.exception.BusinessViolationException.TooFarFromStationException;
import com.dontaza.dontazabackend.global.exception.ResourceException.StationNotFoundException;
import com.dontaza.dontazabackend.station.domain.GeoPoint;
import com.dontaza.dontazabackend.station.domain.Station;
import com.dontaza.dontazabackend.station.domain.StationRepository;
import com.dontaza.dontazabackend.station.dto.StationVerifyRequest;
import com.dontaza.dontazabackend.station.dto.StationVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {

    private static final int PROXIMITY_RADIUS_METERS = 100;

    private final StationRepository stationRepository;

    public StationVerifyResponse verifyProximity(StationVerifyRequest request) {
        Station station = findByStationNo(request.stationNo());
        GeoPoint userLocation = new GeoPoint(request.lat(), request.lng());
        int distance = station.distanceMetersTo(userLocation);
        boolean withinRange = distance <= PROXIMITY_RADIUS_METERS;

        return new StationVerifyResponse(
                station.getId(),
                station.getName(),
                station.getLat(),
                station.getLng(),
                withinRange,
                distance
        );
    }

    public Station findByStationNo(String stationNo) {
        return stationRepository.findByNumber(stationNo)
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
