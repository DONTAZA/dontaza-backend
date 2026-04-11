package com.dontaza.dontazabackend.member.api;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.member.dto.MemberResponse;
import org.springframework.web.bind.annotation.GetMapping;

public interface MemberApi {

    @GetMapping("/me")
    SuccessResponse<MemberResponse> getMyInfo();
}
