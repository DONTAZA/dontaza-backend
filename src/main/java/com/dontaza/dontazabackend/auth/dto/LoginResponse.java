package com.dontaza.dontazabackend.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewUser,
        MemberInfo user
) {

    public record MemberInfo(
            Long id,
            String nickname,
            String profileImageUrl
    ) {
    }
}
