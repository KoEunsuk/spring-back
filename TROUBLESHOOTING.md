#  Troubleshooting (문제 해결 가이드)

개발 과정에서 자주 발생하는 문제와 해결 방법을 정리합니다.

---

### 🚨 **문제 1: 로그인(`POST /api/auth/login`) 시 `401 Unauthorized` 에러 발생**
`signup`은 되는데 `login`만 실패하는 경우입니다.

- **원인**: URL 접근 권한 문제가 아니라, **로그인 인증 절차 자체의 실패**입니다.
- **체크리스트**:
    1.  **비밀번호 암호화**: `AuthService`의 `signup` 메서드에서 `passwordEncoder.encode()`를 사용해 비밀번호를 암호화해서 DB에 저장했는지 확인하세요.
    2.  **사용자(이메일) 존재 여부**: Postman에서 요청하는 `email`이 DB `users` 테이블에 실제로 존재하는지 확인하세요. 오타가 없는지 확인합니다.
    3.  **`SecurityConfig` 경로 확인**: `.requestMatchers("/api/auth/**").permitAll()` 설정이 `AuthController`의 `@RequestMapping("/api/auth")`와 정확히 일치하는지 다시 확인하세요.

---

### 🚨 **문제 2: API 응답이 오지 않고 `HttpMediaTypeNotAcceptableException` 발생**
- **원인**: 서버가 Java 객체를 **JSON으로 변환하는 데 실패**한 경우입니다.
- **체크리스트**:
    1.  **`jackson-databind` 라이브러리 확인**: `build.gradle`에 `implementation 'org.springframework.boot:spring-boot-starter-web'` 의존성이 제대로 포함되어 있는지 확인합니다.
    2.  **DTO에 `@Getter` 확인**: 클라이언트에게 응답으로 보낼 DTO 클래스(예: `JwtResponseDto`, `UserDetailDto`)에 **`@Getter` 어노테이션이 빠졌는지** 확인하세요. Jackson은 getter를 통해 필드 값을 읽어옵니다.

---

### 🚨 **문제 3: 비밀번호 변경 후에도 옛날 토큰이 계속 사용됨**
- **원인**: 토큰 무효화 로직의 시간 비교(`tokenIssuedAt.isBefore(passwordChangedAt)`)가 `false`로 평가되는 경우입니다.
- **체크리스트**:
    1.  **시간 기록 확인**: 비밀번호 변경 후, DB `users` 테이블의 `password_changed_at` 컬럼에 **UTC 기준의 현재 시간**이 제대로 기록되었는지 확인하세요. `null`이라면 `UserService`의 `changeMyPassword` 메서드에 시간 기록 로직이 누락된 것입니다.
    2.  **`Instant` 타입 통일**: 시간 관련 필드(`passwordChangedAt`, 토큰 생성 시점 등)를 모두 타임존 문제에서 자유로운 `Instant` 타입으로 통일했는지 확인하세요. `LocalDateTime`과 `Date`를 혼용하면 문제가 발생할 수 있습니다.
    3.  **테스트 절차 확인**: '옛날 토큰'을 정확히 복사해두고 테스트하는지 확인하세요. 실수로 비밀번호 변경 후 새로 로그인해서 받은 '새 토큰'으로 테스트하고 있을 수 있습니다.
- **참고**:
  - java.time 패키지의 다른 타입들은 아래와 같은 목적에 맞게 사용하시면 됩니다.
    1. Instant: 특정 시점 (Timestamp). UTC 기준이며, 시간대 문제에서 자유롭습니다.
       - 사용 예시: createdAt, updatedAt, passwordChangedAt, actualDepartureTime (실제 출발 시각), actualArrivalTime (실제 도착 시각)
    2. LocalDate: 시간이나 시간대 정보가 없는 날짜. 
       - 사용 예시: birthDate (생년월일), dispatchDate (배차 일자)
    3. LocalTime: 날짜나 시간대 정보가 없는 시간.
       - 사용 예시: openingHour (영업 시작 시간), scheduledDeparture (예정 출발 시간)

---

### 🚨 **문제 4: `LazyInitializationException` 발생**
- **원인**: `@Transactional` 범위 밖에서 지연 로딩(`FetchType.LAZY`)으로 설정된 연관 엔티티에 접근하려고 할 때 발생합니다.
- **해결책**:
    - 연관된 엔티티의 필드에 접근하는 모든 로직(예: DTO 변환)은 **반드시 `@Transactional`이 붙은 서비스 메서드 안에서** 모두 완료해야 합니다.
    - 컨트롤러나 뷰(View) 계층에서 엔티티의 lazy-loading 필드에 직접 접근하지 마세요.

---

### 🚨 **문제 5: `NullPointerException` 발생**
- **원인 1: 컬렉션 미초기화**: `@OneToMany` 필드를 `new ArrayList<>()`로 초기화하지 않은 상태에서 `.getUsers().add()`를 호출하는 경우.
- **원인 2: 연관 엔티티 미확인**: DTO 변환 시 `user.getOperator().getId()`처럼 연관 객체가 `null`일 가능성을 확인하지 않고 바로 메서드를 호출하는 경우.
- **해결책**:
    - 모든 `@OneToMany` 필드는 `private List<Type> items = new ArrayList<>();` 와 같이 선언과 동시에 초기화합니다.
    - DTO 변환 로직 등에서 다른 엔티티에 접근할 때는 항상 `if (user.getOperator() != null)` 과 같은 null 체크를 추가합니다.

---

# 트러블슈팅: Spring Security와 WebSocket 연동 시 권한 없음(Access Denied) 문제 해결기

## 1. 개요 (Overview)
- **기술 스택**: Spring Boot, Spring Security 6+, STOMP over WebSocket, JWT

- **문제 현상**: 유효한 JWT 토큰을 사용했음에도 불구하고, 특정 권한이 필요한 WebSocket 토픽 구독(SUBSCRIBE) 및 메시지 발행(SEND) 요청이 모두 AccessDeniedException으로 실패함.

- **최종 원인**: 최신 컴포넌트 기반 Spring Security 아키텍처와 deprecated된 구형 WebSocket 보안 설정 방식의 충돌로 인한 인터셉터 실행 순서 문제.

- **해결 전략**: 프레임워크의 예측 불가능한 자동 설정을 배제하고, 인증과 인가를 모두 직접 제어하는 통합 ChannelInterceptor를 구현하여 문제 해결.

## 2. 문제 상황 (Problem Statement)

- REST API에 대한 JWT 인증/인가는 정상적으로 동작했으나, WebSocket 통신에서는 모든 요청이 권한 없음으로 차단되었다. 로그 분석 결과, CONNECT 단계는 통과하지만 SUBSCRIBE나 SEND 단계에서 AccessDeniedException이 발생했다. 특이한 점은, 서버 로그상으로는 사용자 인증 정보(Authentication 객체)가 정상적으로 생성되었음에도 불구하고, 정작 권한 검사 시점에는 이 정보가 제대로 사용되지 못하는 것처럼 보였다.

## 3. 트러블슈팅 과정 (Troubleshooting Process)
- 단순한 코드 오류가 아닌, 프레임워크의 깊은 레벨에서 발생하는 문제임을 직감하고 다음과 같은 체계적인 과정을 통해 원인을 추적했다.

- ### 3.1. 1차 가설: 데이터 및 기본 설정 오류
  - 가설: 사용자 DB 권한, ROLE_ 접두사 누락 등 기본적인 데이터 또는 설정 문제일 것이다.

  - 검증: DB 데이터, hasRole vs hasAuthority 차이, CustomUserDetails의 권한 생성 로직을 모두 검토했으나 이상 없음을 확인했다.

  - 결론: 문제는 데이터 레벨이 아니었다.

- ### 3.2. 2차 가설: 프레임워크의 예측 불가능한 동작
  - 가설: 프레임워크의 내부 동작이 꼬여서, 정상적으로 생성된 인증 정보가 인가 로직으로 전달되지 않고 유실될 것이다.

  - 검증:

    - 설정 파일 통합: 분리되어 있던 SecurityConfig와 WebSocketSecurityConfig를 하나로 통합하여 Bean 생명주기를 일치시켜 보았으나 실패했다.
    
    - hashCode() 계약 검증: UserDetails 객체가 Spring Security 내부의 해시 기반 컬렉션에서 유실될 가능성을 고려하여 equals/hashCode 계약을 검토하고 수정했으나, 근본 원인은 아니었다.
 
    - @Order를 통한 순서 강제: Spring 공식 문서에 따라, 인증 인터셉터를 담은 WebSocketMessageBrokerConfigurer에 @Order를 부여하여 실행 순서를 최우선으로 강제했다. 하지만 이 방법 역시 통하지 않았다.

    - 결론: 표준적인 해결책들이 모두 실패함에 따라, 프레임워크의 아키텍처 자체에 대한 근본적인 의심을 하게 되었다.

- ### 3.3. 근본 원인 발견: Deprecated API와 아키텍처 충돌
  - 원인: 모든 문제의 시작은 WebSocket 보안 설정을 위해 사용했던 AbstractSecurityWebSocketMessageBrokerConfigurer가 deprecated된 API였다는 점이다.

  - 분석:

    - HTTP 보안: @Bean SecurityFilterChain을 사용하는 최신 컴포넌트 기반 아키텍처.

    - WebSocket 보안: 클래스를 상속받아 메서드를 오버라이딩하는 구형 아키텍처.

  - 이 두 개의 서로 다른 시대의 아키텍처를 함께 사용하자, 스프링은 내부적으로 보안 컴포넌트들을 올바르게 연결하지 못했다. 그 결과, @Order같은 명시적인 지시마저 무시될 정도로 인터셉터 체인의 구성이 예측 불가능하게 꼬여버린 것이다. 인증을 처리하는 StompHandler보다 인가를 처리하는 스프링의 인터셉터가 먼저 실행되면서 모든 요청이 실패하는 현상이 발생했다.

## 4. 최종 해결책 및 아키텍처 (Final Solution & Architecture)
   - 프레임워크의 불안정한 자동 설정에 의존하는 것을 포기하고, 모든 보안 흐름을 직접 제어하는 가장 확실한 방법을 선택했다.

- ### 4.1. 전략: 통합 보안 인터셉터 구현
  - 인증과 인가의 책임을 모두 수행하는 단일 ChannelInterceptor(StompHandler)를 구현하여, 외부 요인에 의해 보안 흐름이 깨질 가능성을 원천적으로 차단했다.

- ### 4.2. 최종 아키텍처
  - SecurityConfig: 순수 HTTP 보안만 담당하도록 역할을 축소했다.

  - WebSocketConfig: @Order를 통해 StompHandler가 무조건 가장 먼저 실행되도록 순서만 보장한다.

  - StompHandler:

    - 모든 메시지(CONNECT, SUBSCRIBE, SEND)에서 JWT 헤더를 추출해 인증을 수행한다.

    - 인증된 Authentication 객체를 바탕으로, 메시지의 destination을 확인하여 인가 로직을 직접 수행한다.

    - 클라이언트 (React): 매 요청(subscribe, publish)마다 Authorization 헤더를 포함하여 완전한 Stateless 원칙을 따른다.

- ### 4.3. (선택적) 설계 개선: 서비스 계층 분리
  - 최종적으로 동작하는 StompHandler의 가독성과 유지보수성을 높이기 위해, 내부의 복잡한 인가 로직을 별도의 WebSocketAuthService로 분리하여 '관심사 분리' 원칙을 적용했다. StompHandler는 인증과 요청 위임만 처리하고, 실제 권한 규칙은 서비스 계층에서 관리하도록 리팩토링했다.

## 5. 결론 및 교훈 (Conclusion & Lessons Learned)
- 이번 트러블슈팅은 단순한 버그 수정을 넘어, 프레임워크를 깊이 이해하는 계기가 되었다.

- 프레임워크의 진화: deprecated된 API는 단순히 '오래된 것'이 아니라, 최신 아키텍처와의 충돌을 일으킬 수 있는 '지뢰'가 될 수 있음을 깨달았다.

- 명시성의 중요성: 프레임워크의 '마법'같은 자동 설정이 실패할 때, 개발자가 직접 제어권을 가져와 명시적으로 로직을 구성하는 것이 가장 확실한 해결책이 될 수 있다.

- 문제 해결 능력: 표면적인 현상에 매몰되지 않고, 아키텍처와 프레임워크의 동작 원리까지 파고들어 근본적인 원인을 찾아내는 체계적인 접근법의 중요성을 체감했다.

- 실용적인 설계: '관심사 분리' 같은 설계 원칙은 이상적인 상황뿐만 아니라, 이처럼 프레임워크의 한계를 우회하는 실용적인 코드를 작성할 때도 코드의 품질을 높이는 핵심적인 도구가 된다.