package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;

import java.time.LocalDateTime;

public record RidingCurrentResponse(
        boolean active,
        Long ridingId,
        String status,
        boolean verifyAvailable,
        LocalDateTime rentedAt
) {

    public static RidingCurrentResponse from(Riding riding) {
        return new RidingCurrentResponse(
                true,
                riding.getId(),
                riding.getStatus().name(),
                riding.isVerifyAvailable(),
                riding.getRentedAt()
        );
    }

    public static RidingCurrentResponse empty() {
        return new RidingCurrentResponse(false, null, null, false, null);
    }
}
