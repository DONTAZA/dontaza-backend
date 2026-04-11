package com.dontaza.dontazabackend.riding.domain;

import com.dontaza.dontazabackend.global.domain.BaseTimeEntity;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.RidingAlreadyEndedException;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.RidingTooShortException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Riding extends BaseTimeEntity {

    private static final int MINIMUM_RIDING_SECONDS = 300;
    private static final int POINTS_PER_KILOMETER = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private RidingStatus status;

    private LocalDateTime rentedAt;

    private String returnStationId;
    private LocalDateTime returnedAt;

    private int distanceMeters;
    private int durationSeconds;
    private int earnedPoints;

    public static Riding rent(Long userId) {
        Riding riding = new Riding();
        riding.userId = userId;
        riding.status = RidingStatus.WAITING_VERIFICATION;
        riding.rentedAt = LocalDateTime.now();
        return riding;
    }

    public void returnBike(String returnStationId, int distance) {
        validateReturnable();
        this.returnStationId = returnStationId;
        this.returnedAt = LocalDateTime.now();
        this.durationSeconds = (int) Duration.between(rentedAt, returnedAt).getSeconds();
        this.distanceMeters = distance;
        this.earnedPoints = calculatePoints(distance);
        this.status = RidingStatus.COMPLETED;
    }

    public void verify() {
        this.status = RidingStatus.IN_PROGRESS;
    }

    public void cancelVerification() {
        this.status = RidingStatus.VERIFICATION_FAILED;
    }

    private void validateReturnable() {
        if (this.status == RidingStatus.COMPLETED || this.status == RidingStatus.VERIFICATION_FAILED) {
            throw new RidingAlreadyEndedException();
        }
        long elapsed = Duration.between(rentedAt, LocalDateTime.now()).getSeconds();
        if (elapsed < MINIMUM_RIDING_SECONDS) {
            throw new RidingTooShortException();
        }
    }

    private int calculatePoints(int distanceMeters) {
        return (distanceMeters / 1000) * POINTS_PER_KILOMETER;
    }
}
