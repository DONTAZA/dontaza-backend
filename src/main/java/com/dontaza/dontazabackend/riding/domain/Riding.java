package com.dontaza.dontazabackend.riding.domain;

import com.dontaza.dontazabackend.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Riding extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private RidingStatus status;

    private String rentStationNo;
    private String rentStationName;
    private int baselineBikeCount;
    private LocalDateTime rentedAt;

    private String returnStationNo;
    private String returnStationName;
    private LocalDateTime returnedAt;

    private int distanceMeters;
    private int durationSeconds;
    private int earnedPoints;

    public static Riding rent(Long userId, String stationNo, String stationName, int bikeCount) {
        Riding riding = new Riding();
        riding.userId = userId;
        riding.status = RidingStatus.WAITING_VERIFICATION;
        riding.rentStationNo = stationNo;
        riding.rentStationName = stationName;
        riding.baselineBikeCount = bikeCount;
        riding.rentedAt = LocalDateTime.now();
        return riding;
    }

    public void verify() {
        this.status = RidingStatus.IN_PROGRESS;
    }

    public void cancelVerification() {
        this.status = RidingStatus.CANCELLED;
    }

    public boolean isRiding() {
        return this.status == RidingStatus.WAITING_VERIFICATION
                || this.status == RidingStatus.IN_PROGRESS;
    }
}
