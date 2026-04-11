package com.dontaza.dontazabackend.member.domain;

import com.dontaza.dontazabackend.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
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

    private String profileImageUrl;

    private int totalPoints;

    public Member(Long kakaoId, String name, String profileImageUrl) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.totalPoints = 0;
    }

    public void updateProfile(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }
}
