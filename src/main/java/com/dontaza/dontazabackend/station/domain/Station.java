package com.dontaza.dontazabackend.station.domain;

import com.dontaza.dontazabackend.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Station extends BaseTimeEntity {

    private static final int EARTH_RADIUS_METERS = 6_371_000;

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    private int availableBikes;

    public Station(String id, String number, String name, double lat, double lng, int availableBikes) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.availableBikes = availableBikes;
    }

    public void updateInfo(String number, String name, double lat, double lng, int availableBikes) {
        this.number = number;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.availableBikes = availableBikes;
    }

    public int distanceMetersTo(GeoPoint target) {
        double dLat = Math.toRadians(target.lat() - this.lat);
        double dLng = Math.toRadians(target.lng() - this.lng);
        double a = haversine(dLat)
                + Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(target.lat())) * haversine(dLng);
        return (int) (EARTH_RADIUS_METERS * 2 * Math.asin(Math.sqrt(a)));
    }

    public boolean isWithinRadius(GeoPoint center, int radiusMeters) {
        return distanceMetersTo(center) <= radiusMeters;
    }

    private double haversine(double value) {
        return Math.pow(Math.sin(value / 2), 2);
    }
}
