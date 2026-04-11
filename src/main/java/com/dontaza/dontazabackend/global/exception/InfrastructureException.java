package com.dontaza.dontazabackend.global.exception;

import com.dontaza.dontazabackend.global.response.ErrorCode;

public class InfrastructureException extends CustomGlobalException {

    protected InfrastructureException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public InfrastructureException(final ErrorCode errorCode, final String message, final Throwable throwable) {
        super(errorCode, message, throwable);
    }

    public static final class ExternalApiException extends InfrastructureException {

        public ExternalApiException(final String message, final Throwable throwable) {
            super(ErrorCode.EXTERNAL_API_FAIL, message, throwable);
        }

        public ExternalApiException(final String message) {
            super(ErrorCode.EXTERNAL_API_FAIL, message);
        }
    }
}
