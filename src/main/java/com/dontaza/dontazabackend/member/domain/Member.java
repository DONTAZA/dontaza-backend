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

    private int totalPoints;

    public Member(Long kakaoId, String name) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.totalPoints = 0;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }
}
