package com.dontaza.dontazabackend.station.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, String> {

    @Query("SELECT s FROM Station s WHERE s.lat BETWEEN :#{#box.minLat} AND :#{#box.maxLat} AND s.lng BETWEEN :#{#box.minLng} AND :#{#box.maxLng}")
    List<Station> findWithinBoundingBox(BoundingBox box);
}
