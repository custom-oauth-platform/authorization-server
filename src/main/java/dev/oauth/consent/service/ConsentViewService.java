package dev.oauth.consent.service;

import dev.oauth.consent.dto.ConsentScopeView;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ConsentViewService {

    public List<ConsentScopeView> parseRequestedScopes(String scope) {
        if (scope == null || scope.isBlank()) {
            return List.of();
        }

        return Arrays.stream(scope.split(" "))
                .filter(s -> !s.isBlank())
                .distinct()
                .map(this::toView)
                .toList();
    }

    private ConsentScopeView toView(String scope) {
        return switch (scope) {
            case "openid" -> new ConsentScopeView(scope, "아이디", "서비스에서 사용자를 식별하기 위한 아이디 정보에 접근합니다.");
            case "name" -> new ConsentScopeView(scope, "이름", "마이페이지에서 이름 정보를 조회합니다.");
            case "gender" -> new ConsentScopeView(scope, "성별", "마이페이지에서 성별 정보를 조회합니다.");
            case "birthdate" -> new ConsentScopeView(scope, "생일", "마이페이지에서 생일 정보를 조회합니다.");
            case "email" -> new ConsentScopeView(scope, "이메일", "마이페이지에서 이메일 정보를 조회합니다.");
            case "profile" -> new ConsentScopeView(scope, "기본 프로필", "레거시 프로필 범위입니다. 항목별 동의에서는 사용하지 않습니다.");
            default -> new ConsentScopeView(scope, scope, "요청 권한입니다.");
        };
    }
}
