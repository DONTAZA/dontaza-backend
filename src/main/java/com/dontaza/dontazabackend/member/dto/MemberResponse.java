package com.dontaza.dontazabackend.member.dto;

import com.dontaza.dontazabackend.member.domain.Member;

public record MemberResponse(
        Long id,
        String name,
        String nickname,
        int totalPoints
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getTotalPoints()
        );
    }
}
