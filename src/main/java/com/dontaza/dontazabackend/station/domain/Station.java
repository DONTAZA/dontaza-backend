package com.dontaza.dontazabackend.station.domain;

public record Station(
        String id,
        String name,
        double lat,
        double lng,
        int availableBikes
) {

    private static final int EARTH_RADIUS_METERS = 6_371_000;

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

    public boolean isWithinBounds(MapBounds bounds) {
        return bounds.contains(this.lat, this.lng);
    }

    private double haversine(double value) {
        return Math.pow(Math.sin(value / 2), 2);
    }
}
