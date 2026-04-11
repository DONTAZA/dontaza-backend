package com.dontaza.dontazabackend.member.dto;

import com.dontaza.dontazabackend.member.domain.Member;

public record PointResponse(
        int totalPoints
) {

    public static PointResponse from(Member member) {
        return new PointResponse(member.getTotalPoints());
    }
}
