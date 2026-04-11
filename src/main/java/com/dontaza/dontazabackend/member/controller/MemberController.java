package com.dontaza.dontazabackend.member.controller;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.member.api.MemberApi;
import com.dontaza.dontazabackend.member.application.MemberService;
import com.dontaza.dontazabackend.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MemberController implements MemberApi {

    private final MemberService memberService;

    @Override
    public SuccessResponse<MemberResponse> getMyInfo() {
        return SuccessResponse.success(HttpStatus.OK, memberService.getMyInfo(getCurrentMemberId()));
    }

    private Long getCurrentMemberId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
