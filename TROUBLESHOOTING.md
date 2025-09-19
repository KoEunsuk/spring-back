# Drive API 프로젝트 트러블슈팅 가이드

## 1. 로그인 시 `401 Unauthorized` 에러 발생

로그인 API (`POST /api/auth/login`) 호출 시 401 에러가 발생하는 경우.

### 문제 현상
- `signup`은 정상 동작하지만 `login`만 401 에러를 반환.
- 서버 로그에 `Full authentication is required...` 메시지 확인됨.

### 원인 및 해결책
- **원인**: URL 접근 권한 문제가 아니라, **로그인 프로세스 자체의 인증 실패**입니다.
- **체크리스트**:
    1.  **비밀번호 불일치**: `AuthService`의 `signup` 메서드에서 `passwordEncoder.encode()`를 사용해 비밀번호를 암호화했는지 확인.
    2.  **사용자 없음**: Postman 요청 Body의 `email`이 DB에 실제로 존재하는지 확인.
    3.  **오타**: Postman 요청 Body의 `email` 또는 `password`에 오타가 없는지 확인.

---

## 2. API 호출 시 `HttpMediaTypeNotAcceptableException` 발생

- **문제 현상**: 특정 API 응답을 생성하지 못하고 406 Not Acceptable 에러 발생.
- **원인**: Spring이 Java 객체를 JSON으로 변환하지 못하는 경우.
- **해결책**:
    1.  **`@Getter` 누락 확인**: 응답 DTO 클래스에 Lombok의 `@Getter` 어노테이션이 있는지 확인합니다. Jackson 라이브러리는 getter를 통해 필드 값을 읽어옵니다.
    2.  **`jackson-databind` 의존성 확인**: `build.gradle` 파일에 `spring-boot-starter-web` 의존성이 제대로 포함되어 있는지 확인합니다.

---

## 3. 비밀번호 변경 후, 이전 토큰이 계속 사용되는 문제

- **문제 현상**: 비밀번호를 변경했음에도, 변경 전 발급받았던 '옛날 토큰'으로 API 접근이 계속 성공함.
- **원인**: 토큰 무효화 로직의 시간 비교 문제.
- **해결책**:
    1.  **테스트 절차 확인**: '옛날 토큰'을 정확히 복사해두고 사용하는지, 테스트 과정에서 실수로 새 토큰을 사용하고 있지 않은지 확인합니다.
    2.  **`Instant` 타입 사용**: 모든 타임스탬프(`passwordChangedAt`, 토큰 발행시간)를 타임존 문제로부터 자유로운 `Instant` 타입으로 통일하여 비교 로직의 정확성을 높입니다. `JwtAuthFilter`의 비교 로직을 재점검합니다.

---

## 4. `NullPointerException` (NPE) 발생

- **문제 현상**: DTO 변환 또는 컬렉션 조작 시 NPE 발생.
- **원인 및 해결책**:
    1.  **컬렉션 필드**: `@OneToMany` 등 엔티티의 컬렉션 필드는 `private List<User> users = new ArrayList<>();` 와 같이 선언 시점에 바로 초기화합니다.
    2.  **연관 엔티티 접근**: DTO 변환 로직에서 `user.getOperator().getId()` 와 같이 연관된 엔티티에 접근할 때는, `if (user.getOperator() != null)` 과 같이 항상 null 체크를 먼저 수행합니다.
    3.  **`Optional` 처리**: `repository.findById()`의 결과는 `.orElseThrow(...)`를 사용하여 '값이 없는 경우'를 항상 처리해 줍니다.