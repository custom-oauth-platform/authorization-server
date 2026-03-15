package dev.oauth.auth;

import dev.oauth.profile.entity.MemberProfile;
import dev.oauth.profile.repository.MemberProfileRepository;
import dev.oauth.user.entity.User;
import dev.oauth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtCustomizer {

    private final UserRepository userRepository;
    private final MemberProfileRepository memberProfileRepository;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            // 발급되는 토큰이 '액세스 토큰'일 경우에만 클레임(데이터) 추가
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Authentication principal = context.getPrincipal();
                Set<String> authorizedScopes = context.getAuthorizedScopes();
                User user = userRepository.findByUsername(principal.getName()).orElse(null);
                MemberProfile profile = memberProfileRepository.findByUserId(principal.getName()).orElse(null);

                // 1. 인증된 사용자의 권한(Role) 추출
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

                // 2. JWT 페이로드에 'roles' 라는 이름으로 권한 정보 삽입
                context.getClaims().claim("roles", authorities);

                if (authorizedScopes.contains("openid")) {
                    context.getClaims().claim("user_id", principal.getName());
                    context.getClaims().claim("preferred_username", principal.getName());
                }
                if (profile != null) {
                    if (authorizedScopes.contains("name")) {
                        addClaimIfPresent(context, "name", profile.getName());
                    }
                    if (authorizedScopes.contains("gender")) {
                        if (profile.getGender() != null) {
                            context.getClaims().claim("gender", profile.getGender().name());
                        }
                    }
                    if (authorizedScopes.contains("birthdate") && profile.getBirthdate() != null) {
                        context.getClaims().claim("birthdate", profile.getBirthdate().toString());
                    }
                    if (authorizedScopes.contains("email")) {
                        addClaimIfPresent(context, "email", profile.getEmail());
                    }
                }
            }
        };
    }

    private void addClaimIfPresent(JwtEncodingContext context, String claimName, String value) {
        if (value != null && !value.isBlank()) {
            context.getClaims().claim(claimName, value);
        }
    }
}
