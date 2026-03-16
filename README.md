# Custom Spring Authorization Server

이 프로젝트는 Spring Authorization Server를 기반으로 구축된 맞춤형 OAuth2.0 및 OpenID Connect(OIDC) 1.0 인가 서버(Authorization Server)입니다. 현재 프로젝트는 다음과 같은 전체 서비스 구조의 핵심 인증 계층을 담당합니다.

- **client-application**: 로그인 버튼과 마이페이지 UI를 제공하는 프론트엔드/클라이언트

- **authorization-server**: 사용자 인증, 동의 화면, 토큰 발급을 담당 (본 레포지토리)

- **resource-server**: 토큰 검증 및 실제 API 데이터(프로필 등) 제공

# ✨ 주요 기능 (Key Features)

- **OAuth 2.0 & OIDC 1.0 지원: 표준 Authorization Code Grant 타입을 지원합니다.**

- **멀티 데이터소스 아키텍처**:

  - **Auth DB**: 인가 서버가 소유하며, 로그인 계정 정보(ID/PW/Role)를 관리합니다.

  - **Profile DB**: 리소스 서버의 DB를 참조하여 JWT 클레임 생성을 위한 상세 정보(이름/성별/생일 등)를 읽어옵니다.

- **커스텀 JWT 토큰 클레임 (JwtCustomizer):**

  - 사용자의 roles 정보를 Access Token에 주입합니다.

  - 요청된 scope에 따라 프로필 DB에서 데이터를 조회하여 name, email, gender, birthdate 등을 동적으로 추가합니다.
 

# 🔄 로그인 흐름 (Auth Flow)

1. **클라이언트 요청**: 사용자가 클라이언트 앱에서 로그인 버튼을 누릅니다.

2. **인증 진행**: 인가 서버의 커스텀 로그인 페이지(/login)로 리다이렉트되어 아이디/비밀번호를 입력합니다.

3. **사용자 동의**: 로그인 성공 후, 요청된 Scope에 대해 사용자가 권한을 허용하는 동의 화면(/oauth2/consent)이 표시됩니다.

4. **토큰 발급**: 인가 코드(Code) 교환 과정을 거쳐 Access Token과 ID Token이 발급됩니다. 이때 JwtCustomizer가 프로필 DB를 조회하여 토큰 내부 내용을 채웁니다.

5. **리소스 접근**: 클라이언트는 발급받은 토큰으로 리소스 서버의 데이터를 안전하게 호출합니다.

# 🛠 데이터베이스 세팅 (Database Setup)

프로젝트 실행 전 아래 SQL을 실행하여 초기 데이터를 생성하세요. 현재 테스트 환경에서는 원활한 로그인을 위해 평문 비밀번호 사용을 권장합니다.

## 1. Auth Database (인증 서버 전용)
```sql

CREATE DATABASE IF NOT EXISTS oauth_authorization_db;
USE oauth_authorization_db;

CREATE TABLE auth_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 비밀번호는 평문으로 입력 시 즉시 인지됩니다. (예: password123)
INSERT INTO auth_member (user_id, password, role) VALUES
('yeong', 'password123', 'ROLE_USER'),
('ironman', 'password123', 'ROLE_USER'),
('spidey', 'password123', 'ROLE_USER');
```

# ⚙️ 설정 및 구조 (Configuration & Structure)

## 1. 디렉토리 구조

```bash
src/main/java/dev/oauth
├── auth        # 로그인 처리, 사용자 인증, JWT 클레임 구성 (JwtCustomizer 등)
├── config      # 인가 서버, 시큐리티, 멀티 데이터소스 설정
├── consent     # 사용자 동의 화면(Scope 가공) 관련 로직
├── profile     # 리소스 서버 DB(프로필) 참조용 엔티티 및 리포지토리
└── user        # 인증용 계정 DB(Auth) 엔티티 및 리포지토리
```

## 2. 레포지토리 역할

- **UserRepository**: auth_member 테이블과 연결되어 아이디/비번 일치 여부를 확인합니다.

- **MemberProfileRepository**: 리소스 서버의 DB를 읽기 전용으로 참조하여 JWT의 상세 내용(Claim)을 채우는 데 사용됩니다.

## 3. 패스워드 처리 (SecurityConfig)

- 비밀번호가 {bcrypt}로 시작하지 않으면 평문 그대로 비교합니다.

- 테스트 중에는 DB에 비밀번호를 직접 입력(예: password123)하여 간편하게 테스트할 수 있습니다.

# 🚀 시작하기 (Getting Started)

## 기본 설정값

- Port: 9000

- Issuer: http://localhost:9000

- Client ID: oauth2-client-app / Secret: secret

- Redirect URI: http://localhost:3000/api/auth/callback/custom-oauth

## 실행 방법

```bash
# DB 설정 후 실행
./gradlew bootRun
```

## ⚠️ 주의사항

- **클라이언트 정보**: 현재 RegisteredClientRepository에 코드상으로 하드코딩되어 있습니다.

- **동의 세션**: InMemory 방식을 사용하므로 서버 재시작 시 사용자의 권한 동의 내역이 초기화됩니다.

- **연동 테스트**: 전체 흐름 확인을 위해 클라이언트 서버와 리소스 서버가 함께 실행 중이어야 합니다.
