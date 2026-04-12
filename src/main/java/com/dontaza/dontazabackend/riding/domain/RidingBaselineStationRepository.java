package com.dontaza.dontazabackend.riding.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RidingBaselineStationRepository extends JpaRepository<RidingBaselineStation, Long> {

    List<RidingBaselineStation> findByRidingId(Long ridingId);

    void deleteByRidingIn(List<Riding> ridings);
}
