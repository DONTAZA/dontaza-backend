package com.dontaza.dontazabackend.admin.controller;

import com.dontaza.dontazabackend.global.response.SuccessResponse;
import com.dontaza.dontazabackend.riding.application.RidingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/riding")
@RequiredArgsConstructor
public class AdminRidingController {

    private final RidingService ridingService;

    @DeleteMapping("/daily-limit")
    public SuccessResponse<Void> resetDailyLimit() {
        ridingService.resetDailyLimit(getCurrentMemberId());
        return SuccessResponse.success(HttpStatus.OK);
    }

    private Long getCurrentMemberId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
