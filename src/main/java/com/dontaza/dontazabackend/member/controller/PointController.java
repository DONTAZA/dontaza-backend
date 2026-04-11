package com.dontaza.dontazabackend.member.controller;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.member.api.PointApi;
import com.dontaza.dontazabackend.member.application.MemberService;
import com.dontaza.dontazabackend.member.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController implements PointApi {

    private final MemberService memberService;

    @Override
    public SuccessResponse<PointResponse> getMyPoints() {
        return SuccessResponse.success(HttpStatus.OK, memberService.getMyPoints(getCurrentMemberId()));
    }

    private Long getCurrentMemberId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
