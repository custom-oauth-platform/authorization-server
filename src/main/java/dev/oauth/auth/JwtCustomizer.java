package dev.oauth.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class JwtCustomizer {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            // 발급되는 토큰이 '액세스 토큰'일 경우에만 클레임(데이터) 추가
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Authentication principal = context.getPrincipal();

                // 1. 인증된 사용자의 권한(Role) 추출
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

                // 2. JWT 페이로드에 'roles' 라는 이름으로 권한 정보 삽입
                context.getClaims().claim("roles", authorities);

                // (선택) 필요한 경우 이메일 등 추가 정보를 DB에서 조회하여 넣을 수 있습니다.
                // context.getClaims().claim("email", principal.getName() + "@example.com");
            }
        };
    }
}