# Drive API Backend Project

## 1. 프로젝트 소개

차량 관제 및 배차 관리 시스템을 위한 백엔드 API 서버입니다. Spring Boot, JPA, Spring Security를 기반으로 운수회사, 사용자(관리자/운전자), 버스, 배차 등의 도메인을 관리하며, JWT를 이용한 안전한 인증/인가 기능을 제공합니다.

---

## 2. 주요 기능

- **사용자 관리**: 역할(관리자/운전자)에 따른 회원가입 및 로그인
- **인증 및 인가**: JWT(JSON Web Token) 기반의 Stateless 인증 시스템
- **내 정보 관리**: 로그인한 사용자의 정보 조회, 수정, 탈퇴
- **보안**: Spring Security를 통한 API 엔드포인트 접근 제어
- **(구현 예정)**: 버스, 배차, 알림 등 핵심 비즈니스 기능

---

## 3. 기술 스택

- **언어**: Java 17+
- **프레임워크**: Spring Boot 3.x, Spring Security, Spring Data JPA (Hibernate)
- **데이터베이스**: MySQL 8.0+
- **라이브러리**:
    - `Lombok`: 보일러플레이트 코드 자동 생성
    - `jjwt`: JWT 생성 및 검증
    - `Validation`: DTO 유효성 검사

---

## 4. API 엔드포인트 요약

### 인증 (`/api/auth`)
| HTTP Method | URL | 설명 | 접근 권한 |
| :--- | :--- | :--- | :--- |
| `POST` | `/signup` | 회원가입 | 누구나 |
| `POST` | `/login` | 로그인 (JWT 발급) | 누구나 |

### 사용자 (`/api/users`)
| HTTP Method | URL | 설명 | 접근 권한 |
| :--- | :--- | :--- | :--- |
| `GET` | `/me` | 내 정보 조회 | 인증된 사용자 |
| `PATCH` | `/me` | 내 정보 수정 | 인증된 사용자 |
| `DELETE` | `/me` | 회원 탈퇴 | 인증된 사용자 |
| `POST` | `/me/password`| 비밀번호 변경 | 인증된 사용자 |

---

## 5. 실행 방법

1.  **데이터베이스 설정**:
    - MySQL 데이터베이스를 생성합니다.
    - `src/main/resources/application.properties` 파일에 자신의 DB 정보(URL, username, password)를 입력합니다.

2.  **JWT 비밀키 설정**:
    - `application.properties` 파일의 `jwt.secret` 값을 충분히 길고 복잡한 문자열로 변경합니다.

3.  **애플리케이션 실행**:
    - `DriveApiApplication.java` 파일을 실행합니다.

---

## 6. 프로젝트 구조

- **`config`**: `SecurityConfig` 등 설정 관련 클래스
- **`controller`**: API 엔드포인트를 정의하는 컨트롤러
- **`domain`**: 핵심 비즈니스 모델
    - **`entity`**: JPA 엔티티 클래스
    - **`enums`**: `Role` 등 열거형 타입
- **`dto`**: 데이터 전송 객체 (Request/Response)
- **`exception`**: 맞춤형 예외 및 `GlobalExceptionHandler`
- **`repository`**: Spring Data JPA 리포지토리 인터페이스
- **`security`**: JWT, `UserDetails` 등 보안 관련 클래스
- **`service`**: 비즈니스 로직을 처리하는 서비스 계층