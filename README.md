# Authorization Server

Spring Authorization Server 기반 OAuth 2.0 / OIDC 인증 서버입니다.

이 서버는 현재 `/mnt/c/OAuth` 아래의 3개 서버 구조에서 인증 서버 역할을 담당합니다.

- `client-application`: 로그인 버튼과 마이페이지 UI를 제공하는 클라이언트 서버
- `authorization-server`: 로그인, 동의 화면, 인가 코드, 토큰 발급을 담당하는 인증 서버
- `resource-server`: 액세스 토큰을 검증하고 마이페이지 데이터를 제공하는 리소스 서버

<br />

## 역할

이 서버가 담당하는 기능은 아래와 같습니다.

- 사용자 로그인 처리
- OAuth 2.0 Authorization Code 발급
- Access Token / ID Token 발급
- 사용자 동의 화면 렌더링
- 동의된 scope 기준 클레임 구성

<br />

## 로그인 흐름

1. 클라이언트 서버에서 로그인 버튼을 누릅니다.
2. 사용자는 auth 서버 로그인 페이지(`/login`)로 이동합니다.
3. 로그인 성공 후 동의 화면(`/oauth2/consent`)이 표시됩니다.
4. 사용자가 동의한 scope 기준으로 인가 코드와 토큰이 발급됩니다.
5. 클라이언트는 발급받은 Access Token으로 resource server에 요청합니다.
6. resource server는 auth server의 issuer/JWK 기준으로 JWT 서명을 검증합니다.
7. resource server는 DB에서 `user_id`가 일치하는 프로필을 조회하고, scope에 맞는 값만 마이페이지에 반환합니다.

<br />

## Scope 정책

현재 등록된 scope는 아래와 같습니다.

- `openid`
- `name`
- `gender`
- `birthdate`
- `email`

정책은 다음과 같습니다.

- `openid`: 로그인 식별용 필수 scope입니다.
- `name`: 동의 화면에 노출되며, 체크하지 않으면 제출되지 않도록 클라이언트 검증이 적용됩니다.
- `gender`, `birthdate`, `email`: 선택 동의 항목입니다.

<br />

## DB 구성

이 프로젝트는 2개의 MySQL 데이터소스를 사용합니다.

### 1. 인증 DB

`spring.datasource.*`

- DB: `oauth_authorization_db`
- 용도: 로그인용 사용자 계정 조회
- 주요 테이블: `auth_member`

사용 정보:

- `user_id`
- `password`
- `role`

### 2. 프로필 DB

`app.datasource.profile.*`

- DB: `oauth_resource_db`
- 용도: 프로필 정보 조회
- 주요 테이블: `resource_member_profile`

사용 정보:

- `user_id`
- `name`
- `email`
- `gender`
- `birthdate`

현재 구조에서는 외래키를 직접 사용하지 않고, auth DB와 resource DB의 `user_id`가 같다고 가정하고 연결합니다.

<br />

## 토큰 구성

### Access Token

resource server API 호출에 사용합니다.

동의된 scope 기준으로 아래 클레임이 포함될 수 있습니다.

- `user_id`
- `preferred_username`
- `name`
- `email`
- `gender`
- `birthdate`
- `roles`

### ID Token

클라이언트가 로그인 직후 사용자 정보를 표시할 때 사용합니다.

현재 ID Token에는 아래 정보가 들어갈 수 있습니다.

- `preferred_username`
- `name`
- `email`

<br />

## 디렉토리 구조

```text
src/main/java/dev/oauth
├── auth
│   ├── CustomUserDetailsService.java
│   ├── JwtCustomizer.java
│   └── LoginController.java
├── client
│   └── service
│       └── ClientLookupService.java
├── config
│   ├── AuthDataSourceConfig.java
│   ├── AuthorizationServerConfig.java
│   ├── ProfileDataSourceConfig.java
│   └── SecurityConfig.java
├── consent
│   ├── controller
│   │   └── ConsentController.java
│   ├── dto
│   │   └── ConsentScopeView.java
│   └── service
│       └── ConsentViewService.java
├── profile
│   ├── entity
│   │   ├── Gender.java
│   │   └── MemberProfile.java
│   └── repository
│       └── MemberProfileRepository.java
└── user
    ├── entity
    │   └── User.java
    └── repository
        └── UserRepository.java
```

디렉토리별 역할은 아래와 같습니다.

- `auth`: 로그인 처리, 사용자 인증, JWT/ID 토큰 클레임 구성
- `client`: 등록된 OAuth 클라이언트 조회
- `config`: 인가 서버, 시큐리티, 데이터소스 설정
- `consent`: 동의 화면 렌더링과 scope 표시 가공
- `profile`: 프로필 DB 엔티티와 리포지토리
- `user`: 로그인용 사용자 계정 엔티티와 리포지토리

<br />

## 기본 설정값

현재 기본 설정은 [`src/main/resources/application.properties`](/mnt/c/OAuth/authorization-server/src/main/resources/application.properties)에 있습니다.

핵심 값:

- Port: `9000`
- Issuer: `http://localhost:9000`
- Client ID: `oauth2-client-app`
- Redirect URI: `http://localhost:3000/api/auth/callback/custom-oauth`

<br />

## 실행 방법

사전 준비:

- Java 17
- MySQL

실행:

```bash
./gradlew bootRun
```

기본 주소:

- Authorization Server: `http://localhost:9000`

<br />

## 주의사항

- 현재 클라이언트 등록 정보는 코드에 하드코딩되어 있습니다.
- 동의 정보는 `InMemoryOAuth2AuthorizationConsentService`를 사용하므로 서버 재시작 시 유지되지 않습니다.
- 클라이언트/리소스 서버와 함께 실행해야 전체 로그인 흐름을 확인할 수 있습니다.
