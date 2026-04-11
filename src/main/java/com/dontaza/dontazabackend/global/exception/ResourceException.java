package com.dontaza.dontazabackend.global.exception;

import com.dontaza.dontazabackend.global.response.ErrorCode;

public class ResourceException extends CustomGlobalException {

    protected ResourceException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static final class MemberNotFoundException extends ResourceException {

        public MemberNotFoundException() {
            super(ErrorCode.MEMBER_NOT_FOUND, ErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
    }

    public static final class StationNotFoundException extends ResourceException {

        public StationNotFoundException() {
            super(ErrorCode.STATION_NOT_FOUND, ErrorCode.STATION_NOT_FOUND.getMessage());
        }
    }

    public static final class RidingNotFoundException extends ResourceException {

        public RidingNotFoundException() {
            super(ErrorCode.RIDING_NOT_FOUND, ErrorCode.RIDING_NOT_FOUND.getMessage());
        }
    }
}
