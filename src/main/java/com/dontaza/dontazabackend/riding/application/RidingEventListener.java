package com.dontaza.dontazabackend.riding.application;

import com.dontaza.dontazabackend.member.domain.MemberWithdrawnEvent;
import com.dontaza.dontazabackend.riding.domain.Riding;
import com.dontaza.dontazabackend.riding.domain.RidingBaselineStationRepository;
import com.dontaza.dontazabackend.riding.domain.RidingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RidingEventListener {

    private final RidingRepository ridingRepository;
    private final RidingBaselineStationRepository baselineStationRepository;

    @EventListener
    public void handleMemberWithdrawn(MemberWithdrawnEvent event) {
        List<Riding> ridings = ridingRepository.findByUserId(event.memberId());
        baselineStationRepository.deleteByRidingIn(ridings);
        ridingRepository.deleteAll(ridings);
    }
}
