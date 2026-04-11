package com.dontaza.dontazabackend.riding.application;

import com.dontaza.dontazabackend.global.exception.BusinessViolationException.AlreadyRidingException;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.DailyRidingLimitException;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.TooFarFromStationException;
import com.dontaza.dontazabackend.global.exception.ResourceException.RidingNotFoundException;
import com.dontaza.dontazabackend.riding.domain.Riding;
import com.dontaza.dontazabackend.riding.domain.RidingBaselineStation;
import com.dontaza.dontazabackend.riding.domain.RidingBaselineStationRepository;
import com.dontaza.dontazabackend.riding.domain.RidingRepository;
import com.dontaza.dontazabackend.riding.domain.RidingStatus;
import com.dontaza.dontazabackend.riding.dto.RentRequest;
import com.dontaza.dontazabackend.riding.dto.RentResponse;
import com.dontaza.dontazabackend.riding.dto.ReturnRequest;
import com.dontaza.dontazabackend.riding.dto.ReturnResponse;
import com.dontaza.dontazabackend.riding.dto.RidingCurrentResponse;
import com.dontaza.dontazabackend.riding.dto.VerifyResponse;
import com.dontaza.dontazabackend.station.application.StationService;
import com.dontaza.dontazabackend.station.domain.GeoPoint;
import com.dontaza.dontazabackend.station.domain.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RidingService {

    private static final List<RidingStatus> ACTIVE_STATUSES =
            List.of(RidingStatus.WAITING_VERIFICATION, RidingStatus.IN_PROGRESS, RidingStatus.VERIFICATION_FAILED);

    private static final List<RidingStatus> VERIFIABLE_STATUSES =
            List.of(RidingStatus.WAITING_VERIFICATION, RidingStatus.VERIFICATION_FAILED);

    private final RidingRepository ridingRepository;
    private final RidingBaselineStationRepository baselineStationRepository;
    private final StationService stationService;

    @Transactional
    public RentResponse rent(Long userId, RentRequest request) {
        validateNotAlreadyRiding(userId);
        validateDailyLimit(userId);

        List<Station> nearbyStations = stationService.findNearbyStations(request.lat(), request.lng());
        if (nearbyStations.isEmpty()) {
            throw new TooFarFromStationException();
        }

        Riding riding = Riding.rent(userId);
        ridingRepository.save(riding);

        saveBaselines(riding, nearbyStations);
        return RentResponse.of(riding, nearbyStations);
    }

    @Transactional
    public VerifyResponse verify(Long userId) {
        Riding riding = ridingRepository.findFirstByUserIdAndStatusInOrderByRentedAtDesc(userId, VERIFIABLE_STATUSES)
                .orElseThrow(RidingNotFoundException::new);

        List<RidingBaselineStation> baselines = baselineStationRepository.findByRidingId(riding.getId());
        boolean bikeDecreased = baselines.stream()
                .anyMatch(this::hasBikeCountDecreased);

        if (bikeDecreased) {
            riding.verify();
            return VerifyResponse.success();
        }

        riding.cancelVerification();
        return VerifyResponse.fail();
    }

    @Transactional
    public ReturnResponse returnBike(Long userId, ReturnRequest request) {
        Riding riding = findActiveRiding(userId);
        Station returnStation = stationService.findNearestStation(request.lat(), request.lng());

        List<RidingBaselineStation> baselines = baselineStationRepository.findByRidingId(riding.getId());
        int distance = calculateDistanceFromBaseline(baselines, returnStation);

        riding.returnBike(returnStation.getId(), distance);
        return ReturnResponse.from(riding);
    }

    @Transactional(readOnly = true)
    public RidingCurrentResponse getCurrentRiding(Long userId) {
        Riding riding = findActiveRiding(userId);
        return RidingCurrentResponse.from(riding);
    }

    private Riding findActiveRiding(Long userId) {
        return ridingRepository.findFirstByUserIdAndStatusInOrderByRentedAtDesc(userId, ACTIVE_STATUSES)
                .orElseThrow(RidingNotFoundException::new);
    }

    private void validateNotAlreadyRiding(Long userId) {
        if (ridingRepository.existsByUserIdAndStatusIn(userId, ACTIVE_STATUSES)) {
            throw new AlreadyRidingException();
        }
    }

    private void validateDailyLimit(Long userId) {
        if (ridingRepository.existsByUserIdAndStatusAndRentedAtAfter(
                userId, RidingStatus.COMPLETED, LocalDate.now().atStartOfDay())) {
            throw new DailyRidingLimitException();
        }
    }

    private void saveBaselines(Riding riding, List<Station> stations) {
        List<RidingBaselineStation> baselines = stations.stream()
                .map(s -> new RidingBaselineStation(riding, s.getId(), s.getName(), s.getAvailableBikes()))
                .toList();
        baselineStationRepository.saveAll(baselines);
    }

    private boolean hasBikeCountDecreased(RidingBaselineStation baseline) {
        Station current = stationService.findById(baseline.getStationId());
        return current.getAvailableBikes() < baseline.getBaselineBikeCount();
    }

    private int calculateDistanceFromBaseline(List<RidingBaselineStation> baselines, Station returnStation) {
        GeoPoint returnPoint = new GeoPoint(returnStation.getLat(), returnStation.getLng());
        return baselines.stream()
                .map(b -> stationService.findById(b.getStationId()))
                .mapToInt(s -> s.distanceMetersTo(returnPoint))
                .min()
                .orElse(0);
    }
}
