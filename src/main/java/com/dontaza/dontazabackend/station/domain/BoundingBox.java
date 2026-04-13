package com.dontaza.dontazabackend.station.domain;

public record BoundingBox(double minLat, double maxLat, double minLng, double maxLng) {

    private static final double LAT_PER_METER = 1.0 / 111_320.0;

    public static BoundingBox of(final double lat, final double lng, final int radiusMeters) {
        double deltaLat = LAT_PER_METER * radiusMeters;
        double deltaLng = deltaLat / Math.cos(Math.toRadians(lat));
        return new BoundingBox(lat - deltaLat, lat + deltaLat, lng - deltaLng, lng + deltaLng);
    }
}
