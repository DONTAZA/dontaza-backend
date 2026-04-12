package com.dontaza.dontazabackend.auth.dto;

import com.dontaza.dontazabackend.member.domain.Member;

public record LoginResponse(
        boolean isNewUser,
        MemberInfo user
) {

    public record MemberInfo(
            Long id,
            String nickname,
            String profileImageUrl,
            boolean termsAgreed
    ) {
    }

    public static LoginResponse from(boolean isNewUser, Member member) {
        return new LoginResponse(
                isNewUser,
                new MemberInfo(member.getId(), member.getNickname(), member.getProfileImageUrl(), member.isTermsAgreed())
        );
    }
}
