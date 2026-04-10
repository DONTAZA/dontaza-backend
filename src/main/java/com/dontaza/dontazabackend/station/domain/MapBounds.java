package com.dontaza.dontazabackend.station.domain;

public record MapBounds(GeoPoint sw, GeoPoint ne) {

    public boolean contains(double lat, double lng) {
        return lat >= sw.lat() && lat <= ne.lat()
                && lng >= sw.lng() && lng <= ne.lng();
    }
}
