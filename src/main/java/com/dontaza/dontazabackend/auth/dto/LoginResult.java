package com.dontaza.dontazabackend.auth.dto;

import com.dontaza.dontazabackend.member.domain.Member;

public record LoginResult(
        String accessToken,
        String refreshToken,
        boolean isNewUser,
        Member member
) {
}
