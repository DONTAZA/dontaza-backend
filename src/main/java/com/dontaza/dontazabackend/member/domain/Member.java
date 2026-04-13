package com.dontaza.dontazabackend.member.domain;

import com.dontaza.dontazabackend.global.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long kakaoId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    private int totalPoints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean termsAgreed;

    public Member(final Long kakaoId, final String name, final String profileImageUrl) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.nickname = NicknameGenerator.generate();
        this.profileImageUrl = profileImageUrl;
        this.totalPoints = 0;
        this.role = Role.USER;
        this.termsAgreed = false;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public void agreeToTerms() {
        this.termsAgreed = true;
    }

    public void updateProfile(final String name, final String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void addPoints(final int points) {
        this.totalPoints += points;
    }
}
