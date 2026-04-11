package com.dontaza.dontazabackend.member.application;

import com.dontaza.dontazabackend.global.exception.ResourceException.MemberNotFoundException;
import com.dontaza.dontazabackend.member.domain.Member;
import com.dontaza.dontazabackend.member.domain.MemberRepository;
import com.dontaza.dontazabackend.member.dto.MemberResponse;
import com.dontaza.dontazabackend.member.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMyInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return MemberResponse.from(member);
    }

    public PointResponse getMyPoints(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return PointResponse.from(member);
    }
}
