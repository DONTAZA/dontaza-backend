package com.dontaza.dontazabackend.riding.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface RidingRepository extends JpaRepository<Riding, Long> {

    boolean existsByUserIdAndStatusIn(Long userId, Collection<RidingStatus> statuses);
}
