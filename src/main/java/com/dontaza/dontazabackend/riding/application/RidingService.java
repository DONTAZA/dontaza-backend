package com.dontaza.dontazabackend.riding.application;

import com.dontaza.dontazabackend.global.exception.BusinessViolationException.AlreadyRidingException;
import com.dontaza.dontazabackend.global.exception.ResourceException.RidingNotFoundException;
import com.dontaza.dontazabackend.riding.domain.Riding;
import com.dontaza.dontazabackend.riding.domain.RidingRepository;
import com.dontaza.dontazabackend.riding.domain.RidingStatus;
import com.dontaza.dontazabackend.riding.dto.RentRequest;
import com.dontaza.dontazabackend.riding.dto.RentResponse;
import com.dontaza.dontazabackend.riding.dto.ReturnRequest;
import com.dontaza.dontazabackend.riding.dto.ReturnResponse;
import com.dontaza.dontazabackend.riding.dto.RidingCurrentResponse;
import com.dontaza.dontazabackend.riding.dto.VerifyResponse;
import com.dontaza.dontazabackend.station.application.StationService;
import com.dontaza.dontazabackend.station.domain.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RidingService {

    private static final List<RidingStatus> ACTIVE_STATUSES =
            List.of(RidingStatus.WAITING_VERIFICATION, RidingStatus.IN_PROGRESS);

    private final RidingRepository ridingRepository;
    private final StationService stationService;

    @Transactional
    public RentResponse rent(Long userId, RentRequest request) {
        validateNotAlreadyRiding(userId);
        stationService.validateProximity(request.stationNo(), request.lat(), request.lng());

        Station station = stationService.findByStationNo(request.stationNo());
        Riding riding = Riding.rent(userId, station.getNumber(), station.getName(), station.getAvailableBikes());

        ridingRepository.save(riding);
        return RentResponse.from(riding);
    }

    @Transactional
    public ReturnResponse returnBike(Long userId, Long ridingId, ReturnRequest request) {
        Riding riding = ridingRepository.findById(ridingId)
                .orElseThrow(RidingNotFoundException::new);
        stationService.validateProximity(request.stationNo(), request.lat(), request.lng());

        Station rentStation = stationService.findByStationNo(riding.getRentStationNo());
        Station returnStation = stationService.findByStationNo(request.stationNo());
        int distance = rentStation.distanceMetersTo(
                new com.dontaza.dontazabackend.station.domain.GeoPoint(returnStation.getLat(), returnStation.getLng()));

        riding.returnBike(returnStation.getNumber(), returnStation.getName(), distance);
        return ReturnResponse.from(riding);
    }

    @Transactional
    public VerifyResponse verify(Long ridingId) {
        Riding riding = ridingRepository.findById(ridingId)
                .orElseThrow(RidingNotFoundException::new);

        Station station = stationService.findByStationNo(riding.getRentStationNo());
        boolean bikeDecreased = station.getAvailableBikes() < riding.getBaselineBikeCount();

        if (bikeDecreased) {
            riding.verify();
            return VerifyResponse.success();
        }

        riding.cancelVerification();
        return VerifyResponse.fail();
    }

    @Transactional(readOnly = true)
    public RidingCurrentResponse getCurrentRiding(Long userId) {
        Riding riding = ridingRepository.findByUserIdAndStatusIn(userId, ACTIVE_STATUSES)
                .orElseThrow(RidingNotFoundException::new);
        return RidingCurrentResponse.from(riding);
    }

    private void validateNotAlreadyRiding(Long userId) {
        if (ridingRepository.existsByUserIdAndStatusIn(userId, ACTIVE_STATUSES)) {
            throw new AlreadyRidingException();
        }
    }
}
