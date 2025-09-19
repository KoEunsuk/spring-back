# Drive API - 운송 관리 시스템 백엔드 서버

## 1. 프로젝트 소개

본 프로젝트는 운수 회사(Operator), 운전자(Driver), 버스(Bus), 배차(Dispatch) 등을 관리하는 운송 관리 시스템의 백엔드 API 서버입니다. Spring Boot와 JPA, Spring Security, JWT를 사용하여 견고하고 확장성 있는 RESTful API를 제공하는 것을 목표로 합니다.

---

## 2. 주요 기능

- **사용자 관리**: 운수회사(Operator)에 소속된 사용자(User)를 관리합니다.
    - **역할 기반 시스템**: 사용자는 관리자(Admin)와 운전자(Driver) 역할로 구분됩니다. (JPA 상속 전략 사용)
- **인증 및 인가**:
    - **JWT 기반 인증**: 이메일과 비밀번호로 로그인 시 JWT 토큰을 발급합니다.
    - **역할 기반 인가**: `@PreAuthorize`를 사용하여 각 API 엔드포인트에 역할별(예: ADMIN, DRIVER) 접근 제어를 적용합니다.
- **내 정보 관리**: 로그인한 사용자는 자신의 프로필 정보를 조회, 수정, 탈퇴할 수 있습니다.
- **(예정) 버스 및 배차 관리**: 버스 정보 CRUD 및 배차 관련 비즈니스 로직을 구현할 예정입니다.

---

## 3. 기술 스택

- **언어**: Java 17+
- **프레임워크**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **데이터베이스**: MySQL
- **ORM**: Hibernate
- **인증**: JSON Web Token (JWT)
- **라이브러리**: Lombok, JJWT
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