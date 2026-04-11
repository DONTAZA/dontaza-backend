package com.dontaza.dontazabackend.riding.dto;

import com.dontaza.dontazabackend.riding.domain.Riding;
import com.dontaza.dontazabackend.riding.domain.RidingStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public record RidingCurrentResponse(
        Long ridingId,
        String status,
        boolean verifyAvailable,
        LocalDateTime rentedAt
) {

    private static final int VERIFY_WAIT_SECONDS = 300;

    public static RidingCurrentResponse from(Riding riding) {
        boolean canVerify = isVerifiableStatus(riding.getStatus())
                && hasElapsed(riding.getRentedAt());
        return new RidingCurrentResponse(
                riding.getId(),
                riding.getStatus().name(),
                canVerify,
                riding.getRentedAt()
        );
    }

    private static boolean isVerifiableStatus(RidingStatus status) {
        return status == RidingStatus.WAITING_VERIFICATION
                || status == RidingStatus.VERIFICATION_FAILED;
    }

    private static boolean hasElapsed(LocalDateTime rentedAt) {
        return Duration.between(rentedAt, LocalDateTime.now()).getSeconds() >= VERIFY_WAIT_SECONDS;
    }
}
