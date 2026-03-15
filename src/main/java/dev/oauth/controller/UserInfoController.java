package dev.oauth.controller;

import dev.oauth.profile.entity.MemberProfile;
import dev.oauth.profile.repository.MemberProfileRepository;
import dev.oauth.user.entity.User;
import dev.oauth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserInfoController {
    private final UserRepository userRepository;
    private final MemberProfileRepository memberProfileRepository;

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        String username = getStringClaim(jwt, "preferred_username", jwt.getSubject());
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Set<String> scopes = extractScopes(jwt);

        Map<String, Object> claims = new LinkedHashMap<>();
        if (scopes.contains("openid")) {
            claims.put("sub", jwt.getSubject());
            claims.put("preferred_username", user.getUsername());
            claims.put("user_id", user.getUsername());
        }
        MemberProfile profile = memberProfileRepository.findByUserId(user.getUsername()).orElse(null);

        if (scopes.contains("name") && profile != null && profile.getName() != null) {
            claims.put("name", profile.getName());
        }
        if (scopes.contains("gender") && profile != null && profile.getGender() != null) {
            claims.put("gender", profile.getGender().name());
        }
        if (scopes.contains("birthdate") && profile != null && profile.getBirthdate() != null) {
            claims.put("birthdate", formatBirthdate(profile.getBirthdate()));
        }
        if (scopes.contains("email") && profile != null && profile.getEmail() != null) {
            claims.put("email", profile.getEmail());
        }

        return ResponseEntity.ok(claims);
    }

    private String getStringClaim(Jwt jwt, String claimName, String fallbackValue) {
        Object value = jwt.getClaims().get(claimName);
        return value != null ? value.toString() : fallbackValue;
    }

    private Set<String> extractScopes(Jwt jwt) {
        Set<String> scopes = new HashSet<>();
        addScopes(scopes, jwt.getClaims().get("scope"));
        addScopes(scopes, jwt.getClaims().get("scp"));
        return scopes;
    }

    private void addScopes(Set<String> scopes, Object scopeClaim) {
        if (scopeClaim instanceof String scopeString) {
            for (String scope : scopeString.split(" ")) {
                if (!scope.isBlank()) {
                    scopes.add(scope);
                }
            }
            return;
        }
        if (scopeClaim instanceof Collection<?> collection) {
            for (Object value : collection) {
                if (value != null) {
                    scopes.add(value.toString());
                }
            }
        }
    }

    private String formatBirthdate(LocalDate birthdate) {
        return birthdate != null ? birthdate.format(DateTimeFormatter.ISO_DATE) : null;
    }
}
