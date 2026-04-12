# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Test all
./gradlew test

# Run a single test class
./gradlew test --tests "com.dontaza.dontazabackend.SomeTest"

# Run a single test method
./gradlew test --tests "com.dontaza.dontazabackend.SomeTest.methodName"

# Lint (Checkstyle)
./gradlew checkstyleMain
```

## Stack

- Java 21, Spring Boot 3.5, Gradle (Groovy DSL)
- Spring Data JPA + Lombok
- H2 for local/test, MySQL for production

## Architecture

Layered Architecture. Package root: `com.dontaza.dontazabackend`

```
com.dontaza.dontazabackend.{도메인}/
├── api/              # Swagger 명세 (API 인터페이스)
├── controller/       # @RestController 엔드포인트
├── application/      # 서비스 (비즈니스 로직)
├── dto/              # Request/Response DTO
├── domain/           # Entity, JPA Repository — 외부 의존성 없이 순수하게 유지
└── infrastructure/   # 외부 API 연동, 기술 의존성
```

의존 방향: Controller → Application → Domain. Infrastructure는 Domain의 인터페이스를 구현한다.
domain 패키지는 외부 라이브러리/프레임워크 의존 없이 순수하게 유지한다 (JPA 어노테이션 제외).

## Branch Strategy

Trunk-Based Development (TBD)
- `main`에 직접 커밋하거나 수명이 짧은 feature 브랜치에서 빠르게 머지
- feature 브랜치는 1~2일 이내 머지

## Commit Convention

- 헤더(타입) 필수: `feat:`, `fix:`, `refactor:`, `test:`, `chore:`, `docs:`
- body 없이 제목 한 줄로 작성
- 코드 리뷰하기 쉽게 변경 단위를 잘게 쪼개서 커밋 (하나의 커밋 = 하나의 변경 목적)

## 자동 커밋 규칙

기능 구현이 완료되면 빌드(`./gradlew build`)를 실행하고, 통과 시 변경 목적별로 잘게 쪼개서 자동으로 커밋한다.
서브모듈(dontaza-env, dontaza-wiki) 변경이 있으면 서브모듈을 먼저 커밋/푸시한 뒤 메인 레포를 커밋한다.

## Code Rules

Checkstyle(`config/checkstyle/checkstyle.xml`)이 아래 규칙을 자동 검사한다.
Claude Code 훅(`.claude/settings.json`)이 Java 파일 수정 시 Checkstyle을 실행하여 위반을 피드백한다.

### Clean Code (Checkstyle 자동 검사)
- 메서드 길이: 15줄 이하 (빈 줄 제외)
- 메서드 파라미터: 3개 이하 (생성자는 제외, 초과 시 객체로 묶는다)
- 들여쓰기 depth: 2 이하 (early return, 메서드 추출로 해결)
- star import(`*`) 금지, 사용하지 않는 import 금지
- 메서드 네이밍: 동사로 시작, 의도를 명확히 드러낸다
  - 좋은 예: `calculatePointsForDistance`, `verifyBikeRental`
  - 나쁜 예: `process`, `handle`, `doWork`

### OOP
- 객체에게 메시지를 보내라, getter로 꺼내서 외부에서 판단하지 마라
- 도메인 로직은 Entity 안에 둔다, Service에서 절차적으로 나열하지 않는다
- 상속보다 조합을 사용한다

## Test Rules

- **Application(Service)**: 통합 테스트 (`@SpringBootTest`)
- **Controller, Entity, DTO, 유틸 등**: 단위 테스트
- **Repository**: 테스트 작성하지 않음
- BDD 스타일 (Given-When-Then)

```java
@Test
void 라이딩_거리에_따라_포인트가_계산된다() {
    // given
    int distanceMeters = 3200;

    // when
    int points = pointCalculator.calculate(distanceMeters);

    // then
    assertThat(points).isEqualTo(160);
}
```