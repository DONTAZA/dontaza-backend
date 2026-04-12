package com.dontaza.dontazabackend.riding.domain;

import com.dontaza.dontazabackend.global.domain.BaseTimeEntity;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.RidingAlreadyEndedException;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.RidingNotVerifiedException;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.RidingTooShortException;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private RidingStatus status;

    private LocalDateTime rentedAt;

    private String returnStationId;
    private LocalDateTime returnedAt;

    private boolean verifyAvailable;

    private int durationSeconds;
    private int earnedPoints;

    public static Riding rent(Long userId) {
        Riding riding = new Riding();
        riding.userId = userId;
        riding.status = RidingStatus.WAITING_VERIFICATION;
        riding.rentedAt = LocalDateTime.now();
        riding.verifyAvailable = true;
        return riding;
    }

    public void returnBike(String returnStationId, int earnedPoints) {
        validateReturnable();
        this.returnStationId = returnStationId;
        this.returnedAt = LocalDateTime.now();
        this.durationSeconds = (int) Duration.between(rentedAt, returnedAt).getSeconds();
        this.earnedPoints = earnedPoints;
        this.status = RidingStatus.COMPLETED;
    }

    public void verify() {
        this.verifyAvailable = false;
        this.status = RidingStatus.IN_PROGRESS;
    }

    public void cancelVerification() {
        this.verifyAvailable = false;
        this.status = RidingStatus.VERIFICATION_FAILED;
    }

    private void validateReturnable() {
        if (this.status == RidingStatus.COMPLETED) {
            throw new RidingAlreadyEndedException();
        }
        if (this.status == RidingStatus.WAITING_VERIFICATION
                || this.status == RidingStatus.VERIFICATION_FAILED) {
            throw new RidingNotVerifiedException();
        }
        long elapsed = Duration.between(rentedAt, LocalDateTime.now()).getSeconds();
        if (elapsed < MINIMUM_RIDING_SECONDS) {
            throw new RidingTooShortException();
        }
    }

}
