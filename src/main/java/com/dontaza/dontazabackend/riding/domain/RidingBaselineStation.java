package com.dontaza.dontazabackend.riding.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RidingBaselineStation {

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
