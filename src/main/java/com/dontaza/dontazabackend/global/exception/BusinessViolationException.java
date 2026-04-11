package com.dontaza.dontazabackend.global.exception;

import com.dontaza.dontazabackend.global.response.ErrorCode;

public class BusinessViolationException extends CustomGlobalException {

    protected BusinessViolationException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static final class AlreadyRidingException extends BusinessViolationException {

        public AlreadyRidingException() {
            super(ErrorCode.ALREADY_RIDING, ErrorCode.ALREADY_RIDING.getMessage());
        }
    }

    public static final class TooFarFromStationException extends BusinessViolationException {

        public TooFarFromStationException() {
            super(ErrorCode.TOO_FAR_FROM_STATION, ErrorCode.TOO_FAR_FROM_STATION.getMessage());
        }
    }

    public static final class RidingNotVerifiedException extends BusinessViolationException {

        public RidingNotVerifiedException() {
            super(ErrorCode.RIDING_NOT_VERIFIED, ErrorCode.RIDING_NOT_VERIFIED.getMessage());
        }
    }

    public static final class RidingAlreadyEndedException extends BusinessViolationException {

        public RidingAlreadyEndedException() {
            super(ErrorCode.RIDING_ALREADY_ENDED, ErrorCode.RIDING_ALREADY_ENDED.getMessage());
        }
    }
}
