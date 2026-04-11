package com.dontaza.dontazabackend.global.response;

import org.springframework.http.HttpStatus;

public record SuccessResponse<T>(
        T data,
        int status,
        String message
) {

    public static <T> SuccessResponse<T> success(final HttpStatus httpStatus, final T data) {
        return new SuccessResponse<>(data, httpStatus.value(), httpStatus.name());
    }

    public static SuccessResponse<Void> success(final HttpStatus httpStatus) {
        return success(httpStatus, null);
    }
}
