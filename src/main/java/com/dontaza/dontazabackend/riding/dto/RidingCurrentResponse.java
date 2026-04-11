package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;

import java.time.LocalDateTime;

public record RidingCurrentResponse(
        Long ridingId,
        String status,
        boolean verifyAvailable,
        LocalDateTime rentedAt
) {

    public static RidingCurrentResponse from(Riding riding) {
        return new RidingCurrentResponse(
                riding.getId(),
                riding.getStatus().name(),
                riding.isVerifyAvailable(),
                riding.getRentedAt()
        );
    }
}
