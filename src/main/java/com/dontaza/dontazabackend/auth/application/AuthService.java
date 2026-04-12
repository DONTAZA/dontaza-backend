package com.dontaza.dontazabackend.auth.application;

import com.dontaza.dontazabackend.auth.domain.RefreshToken;
import com.dontaza.dontazabackend.auth.domain.RefreshTokenRepository;
import com.dontaza.dontazabackend.auth.dto.KakaoLoginRequest;
import com.dontaza.dontazabackend.auth.dto.LoginResult;
import com.dontaza.dontazabackend.auth.dto.TokenResult;
import com.dontaza.dontazabackend.auth.infrastructure.JwtProvider;
import com.dontaza.dontazabackend.auth.infrastructure.KakaoApiClient;
import com.dontaza.dontazabackend.auth.infrastructure.dto.KakaoTokenResponse;
import com.dontaza.dontazabackend.auth.infrastructure.dto.KakaoUserResponse;
import com.dontaza.dontazabackend.global.exception.BusinessViolationException.InvalidRefreshTokenException;
import com.dontaza.dontazabackend.global.exception.ResourceException.MemberNotFoundException;
import com.dontaza.dontazabackend.member.domain.Member;
import com.dontaza.dontazabackend.member.domain.MemberRepository;
import com.dontaza.dontazabackend.member.domain.MemberWithdrawnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LoginResult kakaoLogin(KakaoLoginRequest request) {
        KakaoTokenResponse kakaoToken = kakaoApiClient.requestToken(
                request.authorizationCode(), request.redirectUri());
        KakaoUserResponse kakaoUser = kakaoApiClient.requestUserInfo(kakaoToken.accessToken());

        boolean isNewUser = !memberRepository.findByKakaoId(kakaoUser.id()).isPresent();
        Member member = findOrCreateMember(kakaoUser);

        String accessToken = jwtProvider.createAccessToken(member.getId());
        String refreshToken = createAndSaveRefreshToken(member.getId());

        return new LoginResult(accessToken, refreshToken, isNewUser, member);
    }

    @Transactional
    public TokenResult refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.delete(refreshToken);

        String newAccessToken = jwtProvider.createAccessToken(refreshToken.getMemberId());
        String newRefreshToken = createAndSaveRefreshToken(refreshToken.getMemberId());

        return new TokenResult(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        kakaoApiClient.unlinkUser(member.getKakaoId());
        deleteMemberData(member);
    }

    @Transactional
    public void withdrawByKakaoId(Long kakaoId) {
        memberRepository.findByKakaoId(kakaoId)
                .ifPresent(this::deleteMemberData);
    }

    private void deleteMemberData(Member member) {
        eventPublisher.publishEvent(new MemberWithdrawnEvent(member.getId()));
        refreshTokenRepository.deleteByMemberId(member.getId());
        memberRepository.delete(member);
    }

    private Member findOrCreateMember(KakaoUserResponse kakaoUser) {
        return memberRepository.findByKakaoId(kakaoUser.id())
                .map(member -> {
                    member.updateProfile(kakaoUser.getNickname(), kakaoUser.getProfileImageUrl());
                    return member;
                })
                .orElseGet(() -> memberRepository.save(
                        new Member(kakaoUser.id(), kakaoUser.getNickname(), kakaoUser.getProfileImageUrl())
                ));
    }

    private String createAndSaveRefreshToken(Long memberId) {
        String token = jwtProvider.createRefreshToken(memberId);
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpiry() / 1000);
        refreshTokenRepository.save(new RefreshToken(memberId, token, expiresAt));
        return token;
    }
}
