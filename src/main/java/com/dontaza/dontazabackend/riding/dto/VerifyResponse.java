package com.dontaza.dontazabackend.riding.dto;

public record VerifyResponse(
        boolean verified
) {

    public static VerifyResponse success() {
        return new VerifyResponse(true);
    }

    public static VerifyResponse fail() {
        return new VerifyResponse(false);
    }
}
