package com.dontaza.dontazabackend.auth.application;

import com.dontaza.dontazabackend.auth.domain.RefreshToken;
import com.dontaza.dontazabackend.auth.domain.RefreshTokenRepository;
import com.dontaza.dontazabackend.auth.infrastructure.KakaoApiClient;
import com.dontaza.dontazabackend.global.exception.ResourceException.MemberNotFoundException;
import com.dontaza.dontazabackend.member.domain.Member;
import com.dontaza.dontazabackend.member.domain.MemberRepository;
import com.dontaza.dontazabackend.riding.domain.Riding;
import com.dontaza.dontazabackend.riding.domain.RidingBaselineStation;
import com.dontaza.dontazabackend.riding.domain.RidingBaselineStationRepository;
import com.dontaza.dontazabackend.riding.domain.RidingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@SpringBootTest

@Transactional
class AuthServiceWithdrawTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RidingRepository ridingRepository;

    @Autowired
    private RidingBaselineStationRepository baselineStationRepository;

    @MockitoBean
    private KakaoApiClient kakaoApiClient;

    @Test
    void 회원_탈퇴_시_모든_데이터가_삭제된다() {
        // given
        Member member = memberRepository.save(new Member(12345L, "홍길동", "https://img.kakao.com/profile.jpg"));
        Long memberId = member.getId();

        RefreshToken refreshToken = new RefreshToken(memberId, "test-refresh-token", LocalDateTime.now().plusDays(14));
        refreshTokenRepository.save(refreshToken);

        Riding riding = Riding.rent(memberId);
        ridingRepository.save(riding);

        baselineStationRepository.save(new RidingBaselineStation(riding, "ST-001", "강남역 1번출구", 10));
        baselineStationRepository.save(new RidingBaselineStation(riding, "ST-002", "강남역 2번출구", 5));

        doNothing().when(kakaoApiClient).unlinkUser(12345L);

        // when
        authService.withdraw(memberId);

        // then
        assertThat(memberRepository.findById(memberId)).isEmpty();
        assertThat(refreshTokenRepository.findByToken("test-refresh-token")).isEmpty();
        assertThat(ridingRepository.findByUserId(memberId)).isEmpty();
        assertThat(baselineStationRepository.findByRidingId(riding.getId())).isEmpty();
    }

    @Test
    void 회원_탈퇴_시_카카오_연결이_해제된다() {
        // given
        Member member = memberRepository.save(new Member(99999L, "테스터", "https://img.kakao.com/test.jpg"));
        doNothing().when(kakaoApiClient).unlinkUser(99999L);

        // when
        authService.withdraw(member.getId());

        // then
        verify(kakaoApiClient).unlinkUser(99999L);
    }

    @Test
    void 존재하지_않는_회원_탈퇴_시_예외가_발생한다() {
        // given
        Long nonExistentMemberId = 999999L;

        // when & then
        assertThatThrownBy(() -> authService.withdraw(nonExistentMemberId))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 카카오_웹훅으로_연결_해제_시_회원_데이터가_삭제된다() {
        // given
        Long kakaoId = 77777L;
        Member member = memberRepository.save(new Member(kakaoId, "웹훅유저", "https://img.kakao.com/webhook.jpg"));
        Long memberId = member.getId();

        Riding riding = Riding.rent(memberId);
        ridingRepository.save(riding);
        baselineStationRepository.save(new RidingBaselineStation(riding, "ST-003", "역삼역", 8));

        // when
        authService.withdrawByKakaoId(kakaoId);

        // then
        assertThat(memberRepository.findByKakaoId(kakaoId)).isEmpty();
        assertThat(ridingRepository.findByUserId(memberId)).isEmpty();
        assertThat(baselineStationRepository.findByRidingId(riding.getId())).isEmpty();
    }

    @Test
    void 존재하지_않는_카카오ID로_웹훅_호출_시_무시된다() {
        // given
        Long nonExistentKakaoId = 000000L;

        // when
        authService.withdrawByKakaoId(nonExistentKakaoId);

        // then — 예외 없이 정상 종료
    }

    @Test
    void 라이딩_기록이_여러개인_회원_탈퇴_시_모두_삭제된다() {
        // given
        Member member = memberRepository.save(new Member(55555L, "다중라이딩", "https://img.kakao.com/multi.jpg"));
        Long memberId = member.getId();

        Riding riding1 = Riding.rent(memberId);
        Riding riding2 = Riding.rent(memberId);
        ridingRepository.save(riding1);
        ridingRepository.save(riding2);

        baselineStationRepository.save(new RidingBaselineStation(riding1, "ST-001", "강남역", 10));
        baselineStationRepository.save(new RidingBaselineStation(riding2, "ST-002", "역삼역", 5));

        doNothing().when(kakaoApiClient).unlinkUser(55555L);

        // when
        authService.withdraw(memberId);

        // then
        assertThat(ridingRepository.findByUserId(memberId)).isEmpty();
        assertThat(baselineStationRepository.findByRidingId(riding1.getId())).isEmpty();
        assertThat(baselineStationRepository.findByRidingId(riding2.getId())).isEmpty();
    }
}
