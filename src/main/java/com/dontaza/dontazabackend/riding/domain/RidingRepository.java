package com.dontaza.dontazabackend.riding.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface RidingRepository extends JpaRepository<Riding, Long> {

    boolean existsByUserIdAndStatusIn(Long userId, Collection<RidingStatus> statuses);

    Optional<Riding> findByUserIdAndStatusIn(Long userId, Collection<RidingStatus> statuses);

    boolean existsByUserIdAndStatusAndRentedAtAfter(Long userId, RidingStatus status, LocalDateTime after);
}
