---
name: dontaza-dev
description: "돈타자 백엔드 기능 개발 오케스트레이터. Service 계층 구현 후 테스트 작성과 API 스펙 업데이트를 병렬로 실행하고, 빌드 검증 후 자동 커밋한다. feat, fix, refactor 등 기능 구현/수정 요청, 테스트 재작성, API 문서 업데이트, 빌드 검증, 자동 커밋 요청 시 이 스킬을 사용할 것."
---

# Dontaza Dev Orchestrator

돈타자 백엔드 기능 개발 워크플로우를 조율하는 오케스트레이터.
Service 계층 구현 → 테스트 + API 스펙 병렬 작성 → 빌드 검증 → 자동 커밋.

## 실행 모드: 서브 에이전트

## 에이전트 구성

| 에이전트 | subagent_type | 역할 | 출력 |
|---------|--------------|------|------|
| test-writer | test-writer | Service 통합 테스트 작성 | `src/test/java/.../{}Test.java` |
| api-spec-writer | api-spec-writer | API 스펙 문서 갱신 | `dontaza-wiki/api-spec.md` |

## 워크플로우

### Phase 0: 컨텍스트 확인

1. 사용자 요청 분석 — 신규 기능 / 기존 수정 / 테스트만 / API 스펙만 판별
2. 실행 범위 결정:
   - **전체 실행**: 기능 구현 + 테스트 + API 스펙 + 빌드 + 커밋
   - **부분 실행 (테스트만)**: test-writer만 호출
   - **부분 실행 (API 스펙만)**: api-spec-writer만 호출
   - **빌드/커밋만**: Phase 3으로 직행

### Phase 1: 기능 구현

메인 에이전트가 직접 수행한다. CLAUDE.md의 아키텍처 규칙을 따른다:

1. **도메인 분석**: 변경 대상 도메인 패키지 구조 파악
2. **Entity 수정/생성**: 도메인 로직은 Entity 안에 둔다
3. **Service 구현**: Controller → Application → Domain 의존 방향 준수
4. **Controller 연결**: RESTful API 규칙 준수
5. **DTO 정의**: Request/Response 분리

구현 완료 후 변경된 파일 목록을 정리하여 Phase 2에 전달한다.

### Phase 2: 테스트 + API 스펙 병렬 실행

**단일 메시지에서 2개 Agent 도구를 동시 호출한다.**

#### test-writer 호출

```
Agent(
  description: "통합 테스트 작성",
  subagent_type: "test-writer",
  model: "opus",
  prompt: """
  다음 Service의 통합 테스트를 작성하라.

  [변경된 Service 클래스 경로]
  [추가/수정된 메서드 목록과 비즈니스 규칙]
  [기존 테스트 파일 경로 (있는 경우)]

  프로젝트의 CLAUDE.md와 에이전트 정의(.claude/agents/test-writer.md)를 읽고 규칙을 따르라.
  """
)
```

#### api-spec-writer 호출

```
Agent(
  description: "API 스펙 업데이트",
  subagent_type: "api-spec-writer",
  model: "opus",
  prompt: """
  다음 API 변경사항을 dontaza-wiki/api-spec.md에 반영하라.

  [변경된 Controller, DTO, API 인터페이스 파일 경로]
  [추가/수정/삭제된 엔드포인트 목록]

  프로젝트의 CLAUDE.md와 에이전트 정의(.claude/agents/api-spec-writer.md)를 읽고 규칙을 따르라.
  기존 api-spec.md의 형식과 스타일을 유지하라.
  """
)
```

두 에이전트 모두 `run_in_background: true`로 실행하여 병렬 처리한다.

### Phase 3: 빌드 검증

1. `./gradlew build` 실행 (컴파일 + 테스트 + Checkstyle)
2. 실패 시:
   - Checkstyle 위반 → 해당 파일 수정
   - 테스트 실패 → 테스트 코드 또는 구현 코드 수정
   - 컴파일 에러 → 원인 파악 후 수정
3. 빌드 성공할 때까지 반복 (최대 3회)

### Phase 4: 자동 커밋

빌드 통과 후, 변경 목적별로 잘게 쪼개서 커밋한다:

1. **서브모듈 먼저**: `dontaza-wiki/` 변경이 있으면 서브모듈 디렉토리에서 커밋/푸시
2. **기능 코드**: `feat:`, `fix:`, `refactor:` 등 타입에 맞게
3. **테스트 코드**: `test:` 타입으로 별도 커밋
4. 커밋 메시지는 body 없이 제목 한 줄

커밋 순서: 서브모듈 → Entity/Domain → Service → Controller/DTO → Test

## 에러 핸들링

| 상황 | 전략 |
|------|------|
| test-writer 실패 | 에러 메시지 확인 후 1회 재시도. 재실패 시 사용자에게 알리고 수동 테스트 작성 안내 |
| api-spec-writer 실패 | dontaza-wiki 접근 불가 시 건너뜀. 사용자에게 서브모듈 상태 확인 요청 |
| 빌드 실패 3회 | 사용자에게 알리고 현재까지의 변경사항 보존. 수동 수정 안내 |
| Checkstyle 위반 | 자동 수정 시도. 메서드 길이 초과 시 메서드 추출 리팩토링 |

## 테스트 시나리오

### 정상 흐름
1. 사용자가 "라이딩 반납 시 포인트 2배 이벤트 기능 추가해줘" 요청
2. Phase 1: RidingService에 포인트 배율 로직 추가, Riding Entity 수정
3. Phase 2: test-writer가 RidingServiceTest에 테스트 추가 + api-spec-writer가 반납 API 응답 필드 갱신 (병렬)
4. Phase 3: `./gradlew build` 통과
5. Phase 4: feat → test 순서로 2개 커밋 생성

### 에러 흐름
1. Phase 2에서 api-spec-writer가 dontaza-wiki 서브모듈 접근 불가로 실패
2. test-writer는 정상 완료
3. 사용자에게 "API 스펙 업데이트 건너뜀 — 서브모듈 확인 필요" 알림
4. Phase 3: 빌드 검증은 정상 진행
5. Phase 4: 기능 + 테스트만 커밋