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

-----

## **API 에러 처리 개선을 통한 서버 안정성 및 응집도 향상**

### \#\# 1. 문제 상황 (Problem)

프로젝트 초기 단계에서 예측 가능한 **비즈니스 규칙 위반** 상황(예: 배차 기록이 있는 버스 삭제 시도)에 대해 `IllegalStateException`을 발생시켜 처리하고 있었습니다. 이 방식은 다음과 같은 문제점을 야기했습니다.

1.  **API 응답의 모호성**: 모든 `IllegalStateException`이 공통 예외 핸들러를 통해 일괄적으로 \*\*`500 Internal Server Error`\*\*로 처리되었습니다. 이는 클라이언트 입장에서 '잘못된 요청'인지 '서버 내부 오류'인지 구분할 수 없게 만들어 디버깅을 어렵게 하고, 잘못된 오류 정보를 전달하는 원인이 되었습니다.
2.  **낮은 코드 가독성 및 유지보수 비용 증가**: 서비스 코드 내에서 `throw new IllegalStateException("에러 메시지")` 구문은 어떤 종류의 비즈니스 규칙 위반인지 한눈에 파악하기 어려웠습니다. 또한, 유사한 비즈니스 예외가 여러 곳에서 발생할 때마다 새로운 예외 메시지를 작성해야 했고, 일관성 있는 관리가 어려워 유지보수 비용을 증가시켰습니다.
3.  **비효율적인 확장 구조**: 만약 각 상황에 맞는 HTTP 상태 코드를 반환하기 위해 개별 커스텀 예외(`BusConflictException`, `DriverConflictException` 등)를 생성한다면, 예외 클래스와 핸들러 메서드의 수가 비즈니스 규칙의 수만큼 폭발적으로 증가하여 코드가 비대해지고 비효율적으로 변할 것이라 예상됐습니다.

-----

### \#\# 2. 해결 과정 (Solution)

이 문제를 해결하기 위해, 모든 비즈니스 예외를 중앙에서 효율적으로 관리하고 명확한 API 응답을 제공하는 것을 목표로 삼았습니다. **`ErrorCode` Enum**과 이를 담는 단일 **`BusinessException`** 클래스를 도입하는 패턴을 적용했습니다.

#### **1단계: `ErrorCode` Enum 설계**

애플리케이션에서 발생할 수 있는 모든 예측 가능한 비즈니스 규칙 위반 상황을 하나의 `ErrorCode` Enum으로 정의했습니다. 각 Enum 상수는 \*\*HTTP 상태 코드(`HttpStatus`)\*\*와 \*\*클라이언트에게 전달될 메시지(`String`)\*\*를 멤버 변수로 갖도록 설계했습니다.

* `409 Conflict`: 상태 충돌 또는 데이터 중복 (예: `BUS_HAS_DISPATCHES`)
* `400 Bad Request`: 유효성 검사 외의 잘못된 요청 값
* `403 Forbidden`: 특정 리소스에 대한 접근 권한 부재
* `500 Internal Server Error`: 데이터 무결성 오류 등 예측 못한 서버 내부 문제

<!-- end list -->

```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BUS_HAS_DISPATCHES(HttpStatus.CONFLICT, "해당 버스에 배차 기록이 존재하여 삭제할 수 없습니다."),
    DRIVER_ALREADY_DISPATCHED(HttpStatus.CONFLICT, "해당 운전자는 요청된 시간에 이미 다른 배차가 있습니다."),
    DISPATCH_NOT_IN_RUNNING_STATE(HttpStatus.CONFLICT, "'운행 중' 상태인 배차만 운행을 종료할 수 있습니다."),
    DRIVER_WITHOUT_OPERATOR(HttpStatus.INTERNAL_SERVER_ERROR, "운전자의 소속 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
```

#### **2단계: `BusinessException` 및 핸들러 구현**

`ErrorCode`를 담을 수 있는 `BusinessException` 커스텀 예외 클래스를 생성했습니다. 그리고 `GlobalExceptionHandler`에 이 예외를 처리하는 **단 하나의 핸들러 메서드**를 추가했습니다. 이 핸들러는 `BusinessException`에서 `ErrorCode`를 꺼내, 미리 정의된 HTTP 상태 코드와 메시지를 사용하여 일관된 `ResponseEntity`를 생성합니다.

* **Before (서비스 코드)**
  ```java
  if (!bus.getDispatches().isEmpty()) {
      throw new IllegalStateException("해당 버스에 배차 기록이 존재하여 삭제할 수 없습니다.");
  }
  ```
* **After (서비스 코드)**
  ```java
  if (!bus.getDispatches().isEmpty()) {
      throw new BusinessException(ErrorCode.BUS_HAS_DISPATCHES);
  }
  ```
* **`GlobalExceptionHandler` 추가**
  ```java
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
      ErrorCode errorCode = ex.getErrorCode();
      log.warn("Business exception: {}", errorCode.getMessage());
      return new ResponseEntity<>(
          ApiResponse.error(errorCode.getMessage(), null),
          errorCode.getStatus()
      );
  }
  ```

-----

### \#\# 3. 결과 및 배운 점 (Result & Key Learnings)

이번 리팩토링을 통해 다음과 같은 긍정적인 결과를 얻을 수 있었습니다.

1.  **명확한 API 응답 체계 확립**: 클라이언트는 이제 비즈니스 규칙 위반 시 `4xx` 계열의 명확한 상태 코드와 메시지를 응답받게 되어, 오류의 원인을 명확히 파악하고 후속 조치를 취하기 용이해졌습니다. 이는 API의 신뢰성을 높이는 중요한 요소가 되었습니다.
2.  **코드 가독성 및 응집도 향상**: 서비스 로직에서 `throw new BusinessException(ErrorCode.BUS_HAS_DISPATCHES);`와 같은 코드는 그 자체로 어떤 비즈니스 오류인지 명확하게 설명해 주는 **자기 서술적인(Self-describing) 코드**가 되었습니다. 또한, 모든 비즈니스 오류 정책이 `ErrorCode` Enum에 중앙 관리되어 코드의 응집도가 높아졌습니다.
3.  **유지보수 효율성 증대**: 새로운 비즈니스 예외 정책이 추가될 때, `ErrorCode`에 새로운 상수만 정의하면 되므로 **확장성**이 크게 향상되었습니다. 별도의 예외 클래스나 핸들러를 추가할 필요가 없어 개발 생산성이 높아졌고, 일관된 오류 처리 정책을 유지하기 쉬워졌습니다.

이번 경험을 통해 단순한 기능 구현을 넘어, **견고하고 확장 가능한 예외 처리 구조**를 설계하는 것의 중요성을 깊이 깨달았습니다. 특히 API 서버에서 일관된 오류 응답은 클라이언트와의 명확한 소통 규약이라는 점에서 핵심적인 품질 요소임을 배울 수 있었습니다.

네, 제공해주신 내용을 트러블슈팅 및 기술 가이드 문서에 사용하기 좋도록 명확하고 전문적인 톤으로 다듬어 드릴게요.

***

## **API 설계 가이드: 상세 정보 조회를 위한 올바른 접근 방식**

### **문제 상황**

하나의 특정 리소스(예: 배차 정보)에 대한 상세 페이지를 구성할 때, 여러 종류의 데이터(기본 정보, 운행 요약, 상세 경로 등)를 클라이언트에 제공해야 합니다. 이때, 모든 정보를 하나의 API로 묶어 제공할지, 아니면 각 데이터 종류에 따라 여러 개의 API로 분리하여 제공할지 결정해야 합니다.

### **두 가지 설계 방식 비교**

#### **방식 A: 기능/데이터별 API 분리 (권장)**

각 데이터의 성격에 맞게 API 엔드포인트를 분리하여 설계하는 방식입니다. 클라이언트는 필요한 데이터를 각각의 API를 통해 개별적으로 요청합니다.

* `GET /api/admin/dispatches/{id}` (배차 기본 정보)
* `GET /api/admin/dispatches/{id}/driving-summary` (운행 요약)
* `GET /api/admin/dispatches/{id}/locations` (상세 운행 경로)

**👍 장점**

* **사용자 경험(UX) 향상**: 페이지 진입 시, 가벼운 기본 정보를 먼저 빠르게 로드하여 사용자에게 화면을 즉시 보여줄 수 있습니다. 용량이 큰 데이터(예: 수천 개의 위치 좌표)는 후속 요청을 통해 비동기적으로 로드하므로, 사용자가 느끼는 **체감 성능이 크게 향상**됩니다.
* **높은 재사용성**: 각 API가 명확한 단일 책임을 가지므로, 다른 페이지나 기능에서 필요한 API만 선택적으로 재사용하기 용이합니다. (예: 배차 목록에서는 기본 정보 API만, 통계 페이지에서는 운행 요약 API만 활용)
* **클라이언트 유연성**: 클라이언트는 현재 화면에 **필요한 데이터만 선택적으로 요청**할 수 있어 불필요한 데이터 전송을 막고 효율성을 높입니다.

**👎 단점**

* **네트워크 요청 횟수 증가**: 여러 번의 API 호출이 필요합니다. 하지만 **HTTP/2** 환경에서는 커넥션 재사용 및 파이프라이닝을 통해 이 단점이 상당 부분 상쇄됩니다.

---

#### **방식 B: 단일 통합 API**

페이지에 필요한 모든 정보를 하나의 거대한 API 응답으로 묶어서 제공하는 방식입니다.

* `GET /api/admin/dispatches/{id}/all-details` (기본 정보, 운행 요약, 경로 등 모든 정보 포함)

**👍 장점**

* **구현의 단순성**: API 호출이 한 번으로 끝나므로 클라이언트 측의 데이터 요청 로직이 단순해집니다.

**👎 단점**

* **느린 초기 응답 속도**: 관련된 모든 데이터를 조회하고 조합하는 과정이 끝날 때까지 클라이언트는 아무런 정보도 받지 못하고 대기해야 합니다. 특히 운행 경로와 같이 **무거운 데이터가 포함되면 시스템 전체 성능에 병목**이 될 수 있습니다.
* **재사용성 저하**: API가 특정 페이지에 강하게 결합되어, 다른 맥락에서 일부 데이터만 재사용하기 매우 어렵습니다.
* **데이터 낭비**: 클라이언트가 기본 정보만 필요한 상황에서도 불필요한 운행 경로 데이터까지 모두 전송받아야 하므로 **네트워크 대역폭을 낭비**하게 됩니다.

### **결론: 최적의 선택**

**현대적인 웹/앱 개발 환경에서는 방식 A (API 분리)가 표준적인 설계로 권장됩니다.**

React, Vue, Angular와 같은 컴포넌트 기반 프레임워크는 여러 API를 비동기적으로 호출하고, 데이터가 도착하는 순서대로 화면을 점진적으로 렌더링하는 방식에 매우 최적화되어 있습니다. 따라서 API를 기능별로 잘게 분리하는 것이 **사용자 경험, 시스템 유연성, 확장성, 유지보수성** 모든 측면에서 더 큰 이점을 제공합니다.

---

# NotificationResponse DTO – Payload 적용

## 1. 문제 상황

알림(Notification) 엔티티는 다양한 알림 타입(DRIVING_WARNING, NEW_DISPATCH_ASSIGNED 등)을 저장하지만, 타입별로 필요한 상세 데이터가 다릅니다.
기존 DTO 설계에서는 모든 필드를 단일 객체에 넣거나 null을 허용하는 방식으로 구현했으나, 다음과 같은 문제가 발생했습니다:

* **불필요한 null 값 증가** → 클라이언트 처리 로직 복잡화
* **UI 구성 유연성 제한** → 타입별로 필요한 정보만 제공하기 어려움
* **DTO 유지보수 어려움** → 알림 타입이 늘어날 때마다 필드 추가 필요

---

## 2. 해결 과정

### 2.1 설계 전략

1. 공통 필드 + 타입별 확장 데이터(payload) 구조 도입
2. payload는 **Map<String, Object>** 또는 향후 필요 시 Record 기반 DTO로 구성
3. DTO 변환 로직에서 **Dispatch null 체크** 및 필드 안전성 확보

---

### 2.2 DTO 구조

#### 공통 필드

| 필드               | 타입               | 설명          |
| ---------------- | ---------------- | ----------- |
| notificationId   | Long             | 알림 고유 ID    |
| message          | String           | 알림 메시지      |
| isRead           | boolean          | 읽음 여부       |
| notificationType | NotificationType | 알림 타입       |
| relatedUrl       | String           | 클릭 시 이동 URL |
| createdAt        | LocalDateTime    | 알림 생성 시간    |

#### 타입별 payload 예시

**DRIVING_WARNING**

| Key           | Value                  |
| ------------- | ---------------------- |
| dispatchId    | Dispatch ID (nullable) |
| vehicleNumber | 차량 번호 (nullable)       |
| driverName    | 운전자 이름 (nullable)      |
| latitude      | 이벤트 발생 위도              |
| longitude     | 이벤트 발생 경도              |

**NEW_DISPATCH_ASSIGNED**

| Key                    | Value       |
| ---------------------- | ----------- |
| dispatchId             | Dispatch ID |
| vehicleNumber          | 차량 번호       |
| driverName             | 운전자 이름      |
| scheduledDepartureTime | 예정 출발 시각    |

---

### 2.3 구현 예시

```java
Map<String, Object> payload = new HashMap<>();
Dispatch dispatch = notification.getDispatch();

switch (notification.getNotificationType()) {
    case DRIVING_WARNING -> {
        if (dispatch != null) {
            payload.put("dispatchId", dispatch.getDispatchId());
            payload.put("vehicleNumber", dispatch.getBus().getVehicleNumber());
            payload.put("driverName", dispatch.getDriver().getUsername());
        }
        payload.put("latitude", notification.getLatitude());
        payload.put("longitude", notification.getLongitude());
    }
    case NEW_DISPATCH_ASSIGNED -> {
        if (dispatch != null) {
            payload.put("dispatchId", dispatch.getDispatchId());
            payload.put("vehicleNumber", dispatch.getBus().getVehicleNumber());
            payload.put("driverName", dispatch.getDriver().getUsername());
            payload.put("scheduledDepartureTime", dispatch.getScheduledDepartureTime());
        }
    }
    default -> payload = null;
}
```

---

## 3. 트러블슈팅 / 주의 사항

### 3.1 `Map.of` vs `HashMap`

* `Map.of`는 null value를 허용하지 않음 → DRIVING_WARNING 등 null 가능 필드에서 런타임 에러 발생
* 해결: **HashMap** 사용

### 3.2 Dispatch null 체크

* 알림에 연관된 배차(dispatch)가 없을 수 있음 → null 안전성 확보 필수

### 3.3 payload 없는 타입 처리

* 기본적으로 null 반환
* 프론트에서 존재 여부로 타입 구분 가능

### 3.4 중복 필드 관리

* 여러 타입에서 공통 필드(dispatchId, vehicleNumber, driverName) 반복됨
* 장기적 리팩토링 시 공통 빌더 메서드로 추출 가능

### 3.5 타입 안정성

* Map<String, Object> 사용 시 컴파일 타임 체크 불가
* 필요 시 Record 또는 DTO 클래스별 payload로 전환 가능

---

## 4. 결과 및 배운 점

1. **DTO 유연성 확보**: 공통 필드 + payload 구조로 알림 타입별 다양한 데이터를 안정적으로 제공 가능
2. **클라이언트 UX 향상**: 필요한 데이터만 payload로 제공 → UI에서 조건부 렌더링 가능
3. **유지보수 효율성**: 알림 타입이 추가되어도 payload 로직만 확장하면 되므로, DTO 구조 변경 최소화
4. **null 안전성 확보**: HashMap + dispatch null 체크로 런타임 예외 방지

---

## 5. 결론

* NotificationResponse DTO에 **payload**를 적용하면, 타입별로 필요한 정보를 효율적으로 전달하면서 기존 공통 필드와 통합 관리 가능
* 프론트/백엔드 간 계약 명확화 및 유지보수 효율성을 크게 개선
* 향후 DTO 확장이 필요한 경우, Map에서 Record/DTO 기반 payload로 전환하여 타입 안정성을 강화 가능

---

### ## 유효성 검사(Validation) 및 논리적 삭제(Soft Delete) 설계 요약

#### ### 문제 1: 유효성 검사의 위치 (DTO vs. 엔티티)

-   **현상**: 클라이언트의 잘못된 요청(예: 필수 필드 누락)이 서비스 로직까지 도달하여 불필요한 연산을 유발하거나, DB 저장 시점에 가서야 오류가 발생함.
-   **고민**: 유효성 검사 어노테이션(`@NotNull` 등)을 DTO에 두어야 할지, 엔티티에 두어야 할지, 아니면 양쪽 다에 두어야 할지 혼란 발생.
-   **최종 결정**: **DTO에만 요청 검증을 적용**하고, 엔티티는 DB 스키마 정의(`@Column(nullable=false)`)에 집중하기로 결정.
    -   **DTO 검증 (`@Valid`)**: API의 '1차 방어선'. 잘못된 요청을 컨트롤러 단에서 가장 먼저 차단하여 즉각적이고 명확한 `400 Bad Request` 피드백을 제공. (Fail-Fast 원칙)
    -   **엔티티 제약조건 (`@Column`)**: DB의 '최후 방어선'. 어떤 경로로 데이터가 오든, 최종적으로 DB에 저장될 데이터의 무결성을 보장.

#### ### 문제 2: 조건부 필수 필드 처리 (예: 운전자만 면허번호 필수)

-   **현상**: `SignupRequestDto`에서 `role`이 `DRIVER`일 경우에만 `licenseNumber`가 필수가 되어야 하는 복합적인 규칙을 기본 어노테이션으로 처리하기 어려움.
-   **고민**: `@Size` 등을 필드에 직접 붙이면 `ADMIN` 가입 시에도 불필요한 검증이 시도될 수 있음.
-   **최종 결정**: **커스텀 유효성 검사(Custom Validator)**를 구현.
    -   `@ValidDriverInfo`라는 클래스 레벨 어노테이션을 생성.
    -   `DriverInfoValidator`라는 실제 검증 로직 클래스에서 "만약 role이 DRIVER라면, licenseNumber가 비어있는지 확인하라"는 비즈니스 규칙을 직접 코드로 구현.

#### ### 문제 3: 데이터 삭제 정책 (물리적 vs. 논리적)

-   **현상**: `Bus`나 `Driver`를 DB에서 물리적으로 삭제(Hard Delete)하면, 해당 리소스를 참조하는 과거의 `Dispatch`(배차) 기록과의 관계가 깨져 **참조 무결성 오류**가 발생. "배차는 남기고 버스/운전자만 삭제"하는 것이 불가능함.
-   **고민**: 기록을 보존하면서 버스/운전자를 시스템에서 제거하는 방법이 필요.
-   **최종 결정**: **논리적 삭제(Soft Delete)** 패턴을 도입.
    -   `Bus`와 `User` 엔티티에 `status` 필드(예: `ACTIVE`, `INACTIVE`)를 추가.
    -   '삭제' 요청이 오면, 실제 `DELETE` 쿼리를 실행하는 대신 `status`를 `INACTIVE` 등으로 변경하는 `deactivate` 서비스 로직을 구현.

#### ### 문제 4: 논리적 삭제와 데이터 조회 간의 충돌

-   **현상**: 논리적 삭제 도입 후, 일반적인 조회 API(예: 배차 가능한 버스 목록)에서 비활성화된 버스가 함께 조회되는 문제 발생.
-   **고민**: `@Where` 어노테이션을 사용하면 편리하지만, "비활성화된 운전자의 과거 배차 기록"처럼 의도적으로 비활성 데이터를 조회해야 할 때 문제가 발생하여 유연성이 떨어짐.
-   **최종 결정**: `@Where`를 사용하지 않고, **Repository 메서드에 `Status` 조건을 명시적으로 추가**하는 규칙을 적용.
    -   **일반 조회**: `findByBusIdAndStatus(id, BusStatus.ACTIVE)`처럼 항상 `ACTIVE` 상태를 조건으로 조회하는 메서드를 만들어 사용. (`findById` 직접 사용 자제)
    -   **특수 조회**: 관리자가 비활성화된 데이터를 포함하여 모든 데이터를 봐야 할 때만 `findAll()`이나 `findById()` 같은 기본 메서드를 사용하도록 역할을 명확히 분리.