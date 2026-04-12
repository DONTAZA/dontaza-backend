package com.dontaza.dontazabackend.riding.domain;

import com.dontaza.dontazabackend.global.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RidingBaselineStation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "riding_id", nullable = false)
    private Riding riding;

    private String stationId;
    private String stationName;
    private int baselineBikeCount;

    public RidingBaselineStation(Riding riding, String stationId, String stationName, int baselineBikeCount) {
        this.riding = riding;
        this.stationId = stationId;
        this.stationName = stationName;
        this.baselineBikeCount = baselineBikeCount;
    }
}
