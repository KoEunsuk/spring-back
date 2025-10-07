# Drive API - 운송 관리 시스템 백엔드 서버

## 1. 프로젝트 소개

본 프로젝트는 운수 회사(Operator), 운전자(Driver), 버스(Bus), 배차(Dispatch) 등을 관리하는 운송 관리 시스템의 백엔드 API 서버입니다. Spring Boot 기반 위에 실시간 통신, 대용량 트래픽 처리, 높은 수준의 보안을 고려한 현대적인 아키텍처를 적용하여, 견고하고 확장성 있는 시스템을 구축하는 것을 목표로 합니다.

---

## 2. 주요 기능

- **사용자 관리**: 운수회사(Operator)에 소속된 관리자(Admin), 운전자(Driver), 버스(Bus) 정보에 대한 CRUD 기능을 제공합니다.
    - **역할 기반 시스템**: 사용자는 관리자(Admin)와 운전자(Driver) 역할로 구분됩니다. (JPA 상속 전략 사용)
- **배차 관리**: 특정 버스와 운전자를 연결하여 운행 스케줄(Dispatch)을 생성, 조회, 수정, 취소하는 핵심 비즈니스 로직을 구현합니다.
- **내 정보 관리**: 로그인한 사용자는 자신의 프로필 정보를 조회, 수정, 탈퇴할 수 있습니다.
- **인증 및 인가**:
    - **JWT 기반 인증**: 이메일과 비밀번호로 로그인 시 JWT 토큰을 발급합니다.
    - **역할 기반 인가**: `@PreAuthorize`를 사용하여 각 API 엔드포인트에 역할별(예: ADMIN, DRIVER) 접근 제어를 적용합니다.
    - **Access/Refresh Token 기반 인증**: 짧은 수명의 Access Token과 긴 수명의 Refresh Token을 분리하여 보안과 사용자 편의성을 모두 확보했습니다.
    - **HttpOnly 쿠키**: 웹 클라이언트의 Refresh Token을 HttpOnly 쿠키에 저장하여 XSS 공격을 방어합니다.
    - **서버 측 로그아웃**: Refresh Token을 DB에서 직접 관리하여, 명시적인 로그아웃 시 토큰을 무효화하는 완전한 세션 제어 기능을 구현했습니다.
- **실시간 관제 시스템**: WebSocket(STOMP)을 이용해 관리자가 운행 중인 차량의 상태를 실시간으로 모니터링합니다.
  - **실시간 위치 및 OBD 정보**: 운행 중인 차량의 GPS 위치와 OBD(차량 진단) 데이터를 고빈도로 수신하여 구독 중인 관리자에게 방송(Broadcast)합니다.
  - **위험 운전 이벤트 처리**: 졸음운전, 급가속 등 위험 이벤트 발생 시, 해당 운수사의 모든 관리자에게 즉시 실시간 알림을 전송합니다.
  - **'블랙박스' 기능**: 위험 이벤트 발생 시, 해당 시점 전후의 위치 및 OBD 데이터를 선별하여 영구 저장합니다.

---

## 3. 기술 스택

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **데이터베이스**: 
  - RDBMS: MySQL - 핵심 데이터 저장
  - In-Memory: Redis - 실시간 데이터 버퍼링, 캐싱
- **ORM**: Hibernate
- **인증**: JSON Web Token (JWT)
- **라이브러리**: Lombok, JJWT, Spring Data Redis
- **빌드 도구**: Gradle

---

## 4. 프로젝트 설정 및 실행 방법

1.  **프로젝트 클론**:
    ```bash
    git clone [저장소 URL]
    ```

2.  **`application.properties` 설정**:
    `src/main/resources/application.properties` 파일을 열어 아래 항목들을 자신의 환경에 맞게 수정합니다.

    ```properties
    # Database 설정
    spring.datasource.url=jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC
    spring.datasource.username=root
    spring.datasource.password=your_password
    
    # JPA 설정
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    
    # JWT 비밀키 및 만료 시간 설정
    # 비밀키는 32바이트 이상의 매우 긴 무작위 문자열을 사용해야 합니다.
    jwt.secret=MySuperSecretKeyForJwtTokenGenerationWhichIsVeryLongAndSecure
    jwt.expiration-ms=86400000 # 24시간
    ```

3.  **빌드 및 실행**:
    프로젝트 루트 디렉토리에서 아래 Gradle 명령어를 실행하거나, IntelliJ의 실행 버튼을 누릅니다.
    ```bash
    ./gradlew bootRun
    ```
    애플리케이션이 `http://localhost:8080`에서 실행됩니다.

---

## 5. API 명세

### Auth API (`/api/auth`)

| Method | URL | 설명 | 권한 |
| :--- | :--- | :--- | :--- |
| `POST` | `/signup` | 회원가입 | 누구나 |
| `POST` | `/login` | 로그인 (JWT 발급) | 누구나 |

### User API (`/api/users`)

| Method | URL | 설명 | 권한 |
| :--- | :--- | :--- | :--- |
| `GET` | `/me` | 내 정보 조회 | 인증된 사용자 |
| `PATCH`| `/me` | 내 정보 수정 | 인증된 사용자 |
| `DELETE`| `/me` | 회원 탈퇴 | 인증된 사용자 |
| `POST` | `/me/password`| 비밀번호 변경 | 인증된 사용자 |

---

## 6. 프로젝트 구조

```
com.drive.backend.drive_api
├── common           // 공통 응답 클래스 (ApiResponse.java)
├── config           // SecurityConfig, WebConfig 등 설정 파일
├── domain           // 핵심 비즈니스 모델
│   ├── entity       // JPA 엔티티 (User, Driver, Bus 등)
│   └── enums        // Role, Grade 등 열거형 타입
├── dto              // 데이터 전송 객체
│   ├── request      // 요청 DTO
│   └── response     // 응답 DTO
├── exception        // 맞춤형 예외 및 전역 예외 처리기
├── repository       // Spring Data JPA 리포지토리
├── security         // Spring Security 및 JWT 관련 클래스
│   ├── jwt          // JwtTokenProvider, JwtAuthFilter 등
│   └── userdetails  // CustomUserDetails, CustomUserDetailsService
└── service          // 비즈니스 로직을 처리하는 서비스 계층
└── controller       // API 엔드포인트를 정의하는 컨트롤러 계층
```

## + 인증/인가 관련 : JWT 기반 Full-Stack 보안 아키텍처 분석: REST API부터 WebSocket까지

### 1. 아키텍처 목표 및 핵심 원칙

-   **통합 인증 시스템**: 일반적인 로그인/회원가입과 실시간 WebSocket 통신이 **동일한 JWT 토큰**을 사용하여 인증되도록 한다.
-   **Stateless (무상태성)**: 서버는 세션을 저장하지 않는다. 모든 요청(HTTP API, WebSocket 메시지)은 그 자체로 필요한 모든 인증 정보(JWT)를 포함해야 한다.
-   **역할 기반 접근 제어 (RBAC)**: 사용자의 역할(`ROLE_ADMIN`, `ROLE_DRIVER` 등)에 따라 API 엔드포인트 접근 및 WebSocket 메시지 발행/구독을 정밀하게 제어한다.

---

### 2. 전체 흐름 분석



사용자가 웹 서비스에 가입하고 실시간 기능을 사용하기까지의 전체 흐름은 크게 두 부분으로 나뉩니다: **초기 인증을 위한 REST API 통신**과 **실시간 통신을 위한 WebSocket 연결**.

#### Part 1: 초기 인증 - REST API를 통한 JWT 발급

이 단계는 사용자가 시스템에 자신을 등록하고, 통신에 사용할 수 있는 출입증(JWT)을 발급받는 과정입니다.

##### **1. 회원가입/로그인 요청**

1.  **Client (React)**: 사용자가 아이디와 비밀번호를 입력하고 '로그인' 버튼을 클릭합니다. 클라이언트는 이 정보를 담아 `/api/auth/signin` 같은 REST API 엔드포인트로 `POST` 요청을 보냅니다.
2.  **`SecurityConfig.java`**: `SecurityFilterChain`이 이 요청을 가장 먼저 가로챕니다.
    -   `.requestMatchers("/api/auth/**").permitAll()` 규칙에 따라, `/api/auth/`로 시작하는 모든 요청은 인증되지 않은 사용자도 접근할 수 있도록 허용됩니다.
3.  **`AuthController`**: 해당 요청은 컨트롤러에 도달하여, `AuthenticationManager`를 통해 실제 사용자 인증 로직(DB에서 아이디/비밀번호 확인)을 수행합니다.

##### **2. JWT 생성 및 발급**

1.  **인증 성공**: 사용자의 아이디와 비밀번호가 유효하면 인증이 성공합니다.
2.  **`JwtTokenProvider.java`**: 인증된 사용자 정보(`Authentication` 객체)를 기반으로 `generateJwtToken()` 메서드를 호출합니다.
    -   사용자의 이메일, ID, 역할(`ROLE_ADMIN` 등)을 토큰의 내용(Payload)에 담습니다.
    -   서버만 알고 있는 비밀 키(`jwt.secret`)로 토큰을 서명하여 위변조를 방지합니다.
3.  **Client (React)**: 서버는 생성된 JWT 토큰을 응답으로 클라이언트에게 보내줍니다. 클라이언트는 이 토큰을 **LocalStorage나 메모리에 안전하게 저장**하여, 이후의 모든 통신에 사용합니다.

#### Part 2: 실시간 통신 - WebSocket 보안

사용자가 JWT를 발급받은 후, 실시간 알림 같은 기능을 사용하기 위해 WebSocket에 연결하는 과정입니다.

##### **1. WebSocket 연결 (HTTP Handshake)**

1.  **Client (React)**: 저장해 둔 JWT를 가지고 WebSocket 연결(`new SockJS(...)`)을 시도합니다.
2.  **`SecurityConfig.java`**: 이 최초 연결 시도는 일반 HTTP `GET` 요청이므로, 다시 `SecurityFilterChain`이 가로챕니다.
    -   `.requestMatchers("/ws/**").permitAll()` 규칙 덕분에, 이 연결 요청은 인증 여부와 상관없이 항상 허용됩니다. 이 단계는 단순히 통신할 수 있는 '전화선'을 연결하는 과정일 뿐입니다.

##### **2. STOMP 메시지 보안 (인증 & 인가)**

전화선이 연결된 후, 실제 데이터가 담긴 STOMP 메시지들이 오고 갑니다. 이 메시지들은 HTTP 필터가 아닌, 우리가 만든 **`StompHandler`가 모두 처리**합니다.

1.  **Client (React)**:
    -   `CONNECT`: 연결 직후, `Authorization` 헤더에 JWT를 담아 `CONNECT` 메시지를 보냅니다.
    -   `SUBSCRIBE`/`SEND`: 토픽을 구독하거나 메시지를 보낼 때도, **매번 `Authorization` 헤더에 JWT를 담아** 보냅니다. (Stateless 원칙)

2.  **`WebSocketConfig.java`**:
    -   `@Order` 어노테이션이 `StompHandler`가 다른 어떤 스프링 컴포넌트보다 **가장 먼저 메시지를 가로채도록 보장**합니다.

3.  **`StompHandler.java` (통합 보안 관문)**:
    -   **`preSend()`**: 서버에 도착하는 모든 STOMP 메시지는 이 메서드를 통과합니다.
    -   **`authenticate()` (인증)**:
        1.  메시지 헤더에서 JWT를 추출하고 `JwtTokenProvider`를 통해 유효성을 검증합니다.
        2.  유효하다면, 토큰 정보로 `Authentication` 객체를 생성하여 **해당 메시지에 일시적으로 설정**합니다 (`accessor.setUser()`).
    -   **`authorize()` (인가)**:
        1.  `authenticate()`를 통해 생성된 `Authentication` 객체가 있는지 확인하여, **토큰 없는 요청을 차단**합니다.
        2.  메시지의 목적지(`destination`, 예: `/app/drive-events`)를 확인합니다.
        3.  `Authentication` 객체 안의 권한(`getAuthorities()`)과 목적지를 비교하여, 미리 정의된 규칙(예: `/app/**` 경로는 `ROLE_DRIVER`만 가능)에 맞는지 **직접 검사**합니다.
        4.  규칙에 맞지 않으면, `AccessDeniedException`을 발생시켜 요청을 즉시 차단하고, 이 예외는 클라이언트에게 STOMP `ERROR` 프레임으로 전달됩니다.

---

### 3. 결론

- 이 아키텍처는 **REST API와 WebSocket이라는 두 가지 다른 통신 방식에 대해, JWT라는 단일 인증 수단을 사용하여 일관된 보안 정책을 적용**하는 현대적인 모델입니다. 특히, 프레임ework의 예측 불가능한 자동 설정에 의존하는 대신, `StompHandler`라는 **단일 책임 지점에서 모든 WebSocket 보안 흐름을 명시적으로 제어**함으로써, 어떤 환경에서도 견고하고 안정적으로 동작하는 시스템을 구축할 수 있었습니다.

---
**7. 주요 아키텍처 결정 및 트러블슈팅**
- 본 프로젝트는 단순한 기능 구현을 넘어, 실제 운영 환경에서 발생할 수 있는 다양한 문제들을 해결하기 위해 다음과 같은 아키텍처를 적용했습니다.

* 7.1. 인증 시스템: Stateless 한계를 넘어 Stateful 한 보안 강화
* 문제점: 일반적인 JWT(Access Token) 방식은 사용자 경험(잦은 재로그인)과 보안(긴 유효기간)에 트레이드오프가 존재하며, 서버가 토큰을 무효화할 수 없는 한계가 있었습니다.

* 해결 방안: Access/Refresh Token 이원화 전략을 채택했습니다. 짧은 수명의 Access Token으로 보안성을 높이고, 긴 수명의 Refresh Token으로 사용자 편의성을 확보했습니다. Refresh Token은 DB에 저장하여 서버가 명시적으로 **로그아웃 처리(무효화)**할 수 있도록 설계하여, Stateless의 한계를 보완했습니다.

* 7.2. 실시간 데이터 처리: DB 부하 분산을 위한 Redis 버퍼 도입
* 문제점: 수백 대의 차량이 보내는 고빈도(초당) 위치/OBD 데이터를 메인 DB(MySQL)에 직접 INSERT할 경우, DB에 심각한 쓰기 부하가 발생하여 시스템 전체 성능이 저하될 수 있습니다.

* 해결 방안: Redis를 중간 버퍼로 활용했습니다. 실시간 데이터는 먼저 Redis의 리스트(List)에 임시 저장(Buffering)하고, @Scheduled 어노테이션을 이용한 스케줄러가 1분에 한 번씩 Redis에 쌓인 데이터를 모아 **Batch Insert**로 DB에 한꺼번에 저장하도록 구현했습니다. 이를 통해 DB I/O 횟수를 획기적으로 줄였습니다.

* 7.3. '블랙박스' 기능: 이벤트 기반 데이터 아카이빙
* 문제점: 모든 고빈도 데이터를 영구 저장하는 것은 비효율적입니다. '위험 운전'과 같은 특정 이벤트가 발생했을 때, 그 주변의 데이터만 선별적으로 저장할 필요가 있었습니다.

* 해결 방안: @Async 비동기 이벤트를 활용했습니다. DrivingEvent가 발생하면, 별도의 스레드에서 Redis 버퍼에 저장된 최근 1분치 데이터를 조회하여, 이벤트 발생 시점 전후 30초의 데이터만 필터링하여 메인 DB에 영구 저장하도록 설계했습니다.

* 7.4. 동시성 제어: Optimistic Lock을 이용한 데이터 정합성 확보
* 문제점: 여러 관리자가 동시에 같은 배차 정보를 수정할 경우, 나중에 저장한 데이터가 이전에 저장한 데이터를 덮어쓰는 'Lost Update' 문제가 발생할 수 있습니다.

* 해결 방안: JPA의 **@Version**을 이용한 **낙관적 락(Optimistic Lock)**을 적용했습니다. 또한, 충돌 발생 확률이 높은 DrivingRecord 업데이트 로직에는 재시도(Retry) 메커니즘을 구현하여, 일시적인 충돌은 시스템이 스스로 해결하도록 만들어 안정성을 높였습니다.

* 7.5. 비동기 처리 및 트랜잭션 분리: 시스템 안정성 향상
* 문제점: 위험 이벤트 처리 로직은 DB 저장, 카운터 업데이트, 알림 전송 등 여러 작업으로 구성됩니다. 만약 알림 전송에 실패했다는 이유로 전체 트랜잭션이 롤백되면, 가장 중요한 이벤트 발생 기록까지 유실될 수 있습니다.

* 해결 방안: **@Async**를 통해 이벤트 처리를 비동기로 전환하여 API 응답성을 확보하고, DB에 이벤트를 최초 저장하는 로직에 **@Transactional(propagation = REQUIRES_NEW)**를 적용하여 트랜잭션을 분리했습니다. 이를 통해 후속 작업이 실패하더라도 이벤트 기록 자체는 반드시 DB에 커밋되도록 보장했습니다.